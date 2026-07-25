package com.app.server.service.impliment;

import com.app.server.dto.request.RegisterRequestDto;
import com.app.server.dto.request.UpdateUserRequestDto;
import com.app.server.dto.response.CustomResponseDto;
import com.app.server.dto.response.RegisterResponseDto;
import com.app.server.exception.AppNotFoundException;
import com.app.server.exception.AppUnAuthorizedException;
import com.app.server.model.Role;
import com.app.server.model.User;
import com.app.server.repository.UserRepository;
import com.app.server.service.UserService;
import com.app.server.util.wallet_service_producer.WalletRMQProducer;
import com.app.server.util.wallet_service_producer.dto.request.CreateWalletRequestDto;
import com.app.server.util.wallet_service_producer.dto.response.WalletResponseDto;
import com.github.mfathi91.time.PersianDate;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    /*
     * IMPORTANT CACHE DESIGN NOTE
     * ----------------------------------------------------------------------
     * Three SEPARATE cache regions are used instead of one shared "userCached"
     * region:
     *   - "userById"            -> key: user id      -> value: User
     *   - "userByUsername"      -> key: username      -> value: User
     *   - "userExistsByUsername"-> key: username      -> value: Boolean
     *
     * Previously, findUserByUsername (returns User) and existUserByUsername
     * (returns Boolean) both wrote into the SAME cache name with the SAME key
     * ("userCached" / #username). That causes cache key collisions:
     * whichever method runs first "poisons" the cache entry for the other one
     * (best case: constant cache misses, worst case: ClassCastException when
     * Spring tries to hand back a Boolean where a User was cached, or vice
     * versa).
     *
     * Also, because username can change via updateUser(), we cannot reliably
     * evict the OLD username-keyed entry using a simple SpEL key expression
     * (the new username is in the request, not the old one). So mutation
     * methods (update/changeRole/delete) evict caches manually through
     * CacheManager, using the username value fetched BEFORE the mutation.
     */

    private static final String CACHE_USER_BY_ID = "userById";
    private static final String CACHE_USER_BY_USERNAME = "userByUsername";
    private static final String CACHE_USER_EXISTS_BY_USERNAME = "userExistsByUsername";

    @Value("${application.wallet-service.currency}")
    private String currency;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final WalletRMQProducer walletRMQProducer;
    private final CacheManager cacheManager;
    private final OtpService otpService;

    /**
     * Returns all users sorted by id ascending.
     * Not cached: this is a bulk/listing query and caching it would require
     * invalidating on every single-user mutation, which defeats the purpose.
     */
    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll(Sort.by("id").ascending());
    }





    /**
     * Builds a new (unsaved) User entity from the registration request, off
     * the calling thread. Password is encoded here so the heavy BCrypt work
     * also happens asynchronously.
     */

    public User createUser(RegisterRequestDto req) {
        User created = User.builder()
                .username(req.getUsername())
                .password(passwordEncoder.encode(req.getPassword()))
                .fullName(req.getFullName())
                .phoneNumber(req.getPhoneNumber())
                .roles(Set.of(Role.USER))
                .walletId(null)
                .build();
        return userRepository.save(created);
    }

    /**
     * Registers a new user: creates the wallet (via RabbitMQ RPC) and builds
     * the User entity concurrently, links the returned wallet id, then
     * persists the user.
     * NOTE: previously `CompletableFuture.allOf(...)` was called but its
     * result was discarded (dead code); replaced with a proper `.join()` so
     * both futures are actually awaited together before continuing.
     */
    @Transactional
    @Override
    public RegisterResponseDto registerUser(RegisterRequestDto req) {

        User user = createUser(req);
        CompletableFuture<String> walletFuture = createWalletAysnc();
        String walletId = walletFuture.join();
        user.setWalletId(walletId);
        userRepository.save(user);


        String msg="به صدور24 خوش آمدید.\n" +
                "ثبت نام شما با موفقیت انجام شد.\n" +
                "کد تأیید شما:";

        otpService.sendOtp(user.getPhoneNumber(),msg);


        return RegisterResponseDto.builder()
                .message("کاربر " + user.getUsername() + " با موفقیت ایجاد شد")
                .status(HttpStatus.CREATED.value())
                .details("خوش آمدید")
                .timestamp(new Date())
                .build();
    }

    /**
     * Finds a user by username. Cached in its OWN region ("userByUsername")
     * so it never collides with the id-keyed or exists-keyed caches.
     */
    @Cacheable(value = CACHE_USER_BY_USERNAME, key = "#username")
    @Override
    public User findUserByUsername(String username) {
        return userRepository.findUserByUsername(username)
                .orElseThrow(() -> new AppNotFoundException("کاربر با این نام کاربری پیدا نشد"));
    }

    /**
     * Checks whether a username exists. Cached in a DEDICATED region
     * ("userExistsByUsername") separate from the User-object caches, since
     * this returns a Boolean, not a User.
     */
    @Cacheable(value = CACHE_USER_EXISTS_BY_USERNAME, key = "#username")
    @Override
    public Boolean existUserByUsername(String username) {
        return userRepository.findUserByUsername(username).isPresent();
    }

    /**
     * Finds a user by id. Cached in its own region ("userById").
     */
    @Override
    @Cacheable(value = CACHE_USER_BY_ID, key = "#id")
    public User findUserById(Long id) {
        return userRepository.findUserById(id)
                .orElseThrow(() -> new AppUnAuthorizedException(
                        "کاربری با این آیدی پیدا نشد",
                        "لیست کاربران را مجدد بررسی نمایید"
                ));
    }

    /**
     * Updates a user's profile fields.
     * Because the username itself can change here, we capture the OLD
     * username before mutating, then manually evict every cache entry tied
     * to both the old and the new username plus the id, and repopulate the
     * id/username caches with the fresh entity. Explicit save() is used
     * instead of relying purely on JPA dirty-checking, to make persistence
     * intent explicit.
     */
    @Override
    @Transactional
    public User updateUser(UpdateUserRequestDto req, Long id) {
        User existUser = findUserById(id);
        String oldUsername = existUser.getUsername();

        existUser.setUsername(req.getUsername());
        existUser.setPassword(passwordEncoder.encode(req.getPassword()));
        existUser.setFullName(req.getFullName());
        existUser.setPhoneNumber(req.getPhoneNumber());
        // Roles are intentionally left untouched here; use changeUserRole() for that.

        User saved = userRepository.save(existUser);

        evictUserCaches(oldUsername, saved.getUsername(), id);
        putUserCaches(saved);

        return saved;
    }

    /**
     * Changes a user's roles. Username is unaffected here, so only the
     * id/username caches for the current username need refreshing (no old
     * username to worry about).
     */
    @Transactional
    @Override
    public User changeUserRole(Long id, Set<Role> roles) {
        User existUser = findUserById(id);
        existUser.setRoles(roles);
        User saved = userRepository.save(existUser);

        evictUserCaches(saved.getUsername(), saved.getUsername(), id);
        putUserCaches(saved);

        return saved;
    }

    /**
     * Deletes a user by id and evicts every cache entry associated with
     * them (id, username, and exists-by-username).
     */
    @Override
    public CustomResponseDto deleteUserById(Long id) {
        User user = findUserById(id);
        userRepository.delete(user);

        evictUserCaches(user.getUsername(), user.getUsername(), id);

        return CustomResponseDto.builder()
                .message("کاربر با موفقیت حذف شد")
                .details("")
                .status(200)
                .timestamp(PersianDate.now())
                .build();
    }

    /**
     * Extracts the authenticated User principal from the Authentication
     * object, if present.
     */
    @Override
    public User convertUserFromAuthentication(Authentication auth) {
        if (auth == null) {
            return null;
        }
        Object principal = auth.getPrincipal();
        if (!(principal instanceof User user)) {
            return null;
        }
        return user;
    }

    /**
     * Async wrapper around createWallet(), so wallet creation can run
     * concurrently with building the local User entity during registration.
     */
    public CompletableFuture<String> createWalletAysnc() {
        return CompletableFuture.supplyAsync(this::createWallet);
    }

    /**
     * Requests a new wallet from the wallet-service over RabbitMQ RPC and
     * returns the created wallet's subject/id. Throws explicitly instead of
     * risking a silent NullPointerException if the response is malformed.
     */
    public String createWallet() {
        CreateWalletRequestDto req = CreateWalletRequestDto.builder()
                .sub("")
                .balance(BigDecimal.ZERO)
                .currency(currency)
                .build();

        WalletResponseDto res = walletRMQProducer.createWallet(req);
        if (res == null || res.getData() == null) {
            throw new AppNotFoundException("پاسخ معتبری از سرویس کیف پول دریافت نشد");
        }

        Map<String, Object> data = (Map<String, Object>) res.getData();
        Object sub = data.get("sub");
        if (sub == null) {
            throw new AppNotFoundException("شناسه کیف پول در پاسخ سرویس یافت نشد");
        }
        return sub.toString();
    }

    /**
     * Manually evicts all cache entries related to a user across the three
     * dedicated cache regions. Handles the case where the username changed
     * (oldUsername != newUsername) by clearing both keys.
     */
    private void evictUserCaches(String oldUsername, String newUsername, Long id) {
        evictFromCache(CACHE_USER_BY_ID, id);
        evictFromCache(CACHE_USER_BY_USERNAME, oldUsername);
        evictFromCache(CACHE_USER_EXISTS_BY_USERNAME, oldUsername);
        if (newUsername != null && !newUsername.equals(oldUsername)) {
            evictFromCache(CACHE_USER_BY_USERNAME, newUsername);
            evictFromCache(CACHE_USER_EXISTS_BY_USERNAME, newUsername);
        }
    }

    /**
     * Re-populates the id and username caches with a freshly saved entity,
     * so the very next read hits a warm, correct cache instead of a miss.
     */
    private void putUserCaches(User user) {
        putIntoCache(CACHE_USER_BY_ID, user.getId(), user);
        putIntoCache(CACHE_USER_BY_USERNAME, user.getUsername(), user);
        putIntoCache(CACHE_USER_EXISTS_BY_USERNAME, user.getUsername(), Boolean.TRUE);
    }

    private void evictFromCache(String cacheName, Object key) {
        if (key == null) {
            return;
        }
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.evict(key);
        }
    }

    private void putIntoCache(String cacheName, Object key, Object value) {
        if (key == null) {
            return;
        }
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.put(key, value);
        }
    }

}