package com.app.server.service.impliment;

import com.app.server.dto.request.RegisterRequestDto;
import com.app.server.dto.request.UpdateUserRequestDto;
import com.app.server.dto.response.RegisterResponseDto;
import com.app.server.exception.AppConflicException;
import com.app.server.exception.AppNotFoundException;
import com.app.server.exception.AppUnAuthorizedException;
import com.app.server.model.OtpType;
import com.app.server.model.User;
import com.app.server.repository.UserRepository;
import com.app.server.service.OtpService;
import com.app.server.service.UserService;
import com.app.server.service.impliment.OtpFactory.OtpFactory;
import com.app.server.util.wallet_service_producer.WalletRMQProducer;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final WalletRMQProducer walletRMQProducer;
    private final OtpFactory otpFactory;


    // =========================================================
    // GET ALL USERS
    // =========================================================

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll(
                Sort.by("id").ascending()
        );
    }


    // =========================================================
    // CREATE USER
    // =========================================================

    private CompletableFuture<User> createUser(RegisterRequestDto req) {

        return CompletableFuture.supplyAsync(() -> {

            Optional<User> existingUser =
                    userRepository.findUserByUsername(req.getUsername());

            if (existingUser.isPresent()) {
                throw new AppConflicException(
                        "کاربر با این نام کاربری وجود دارد"
                );
            }

            User user = User.builder()
                    .username(req.getUsername())
                    .password(passwordEncoder.encode(req.getPassword()))
                    .sub(req.getSub())
                    .firstName(req.getFirstName())
                    .lastName(req.getLastName())
                    .nationalCode(req.getNationalCode())
                    .email(req.getEmail())
                    .phoneNumber(req.getPhoneNumber())
                    .walletId(null)
                    .build();

            return userRepository.save(user);
        });
    }


    // =========================================================
    // REGISTER
    // =========================================================

    @Transactional(rollbackOn = Exception.class)
    @Override
    public RegisterResponseDto registerUser(RegisterRequestDto req) {

        User user = createUser(req).join();

        OtpService otpService =
                otpFactory.getService(OtpType.USER);

        otpService.generateOtp(user.getPhoneNumber());

        return RegisterResponseDto.builder()
                .message(
                        "با موفقیت ایجاد شد "
                                + user.getUsername()
                                + " کاربر"
                )
                .status(HttpStatus.CREATED.value())
                .details("خوش آمدید")
                .timestamp(new Date())
                .build();
    }


    // =========================================================
    // FIND BY USERNAME
    // =========================================================

    @Cacheable(
            value = "userCacheByUserName",
            key = "#username",
            unless = "#result == null"
    )
    @Override
    public User findUserByUsername(String username) {

        return userRepository.findUserByUsername(username)
                .orElseThrow(() ->
                        new AppUnAuthorizedException(
                                "کاربر با این نام کاربری پیدا نشد",
                                ""
                        )
                );
    }


    // =========================================================
    // EXISTS BY USERNAME
    // =========================================================

    @Cacheable(
            value = "userExistsByUsername",
            key = "#username"
    )
    @Override
    public Boolean existUserByUsername(String username) {

        return userRepository
                .findUserByUsername(username)
                .isPresent();
    }


    // =========================================================
    // FIND BY ID
    // =========================================================

    @Cacheable(
            value = "userCacheById",
            key = "#id",
            unless = "#result == null"
    )
    @Override
    public User findUserById(Long id) {

        return userRepository.findUserById(id)
                .orElseThrow(() ->
                        new AppUnAuthorizedException(
                                "کاربری با این آیدی پیدا نشد",
                                "لیست کاربران را مجدد بررسی نمایید"
                        )
                );
    }


    // =========================================================
    // FIND BY PHONE
    // =========================================================

    @Override
    public User findUserByPhoneNumber(String phoneNumber) {

        return userRepository
                .findUserByPhoneNumber(phoneNumber)
                .orElseThrow(() ->
                        new AppNotFoundException(
                                "شماره تماس شما اشتباه میباشد"
                        )
                );
    }


    // =========================================================
    // FIND BY SUB
    // =========================================================

    @Override
    public User findUserBySub(String sub) {

        return userRepository.findUserBySub(sub)
                .orElseThrow(() ->
                        new AppUnAuthorizedException(
                                "کاربر با این sub پیدا نشد",
                                ""
                        )
                );
    }


    // =========================================================
    // UPDATE USER
    // =========================================================

    @Transactional
    @Override
    public User updateUser(UpdateUserRequestDto req, Long id) {

        User existingUser = findUserById(id);

        String oldUsername = existingUser.getUsername();
        String oldSub = existingUser.getSub();

        existingUser.setFirstName(req.getFirstName());
        existingUser.setLastName(req.getLastName());
        existingUser.setEmail(req.getEmail());
        existingUser.setPhoneNumber(req.getPhoneNumber());
        existingUser.setNationalCode(req.getNationalCode());

        if (req.getPassword() != null &&
                !req.getPassword().isBlank()) {

            existingUser.setPassword(
                    passwordEncoder.encode(req.getPassword())
            );
        }

        User updatedUser =
                userRepository.save(existingUser);

        /*
         * Cache invalidation
         *
         * چون ممکن است username تغییر کرده باشد،
         * cache مربوط به username قبلی هم باید پاک شود.
         */

        evictUserCaches(
                id,
                oldUsername,
                updatedUser.getUsername(),
                oldSub
        );

        /*
         * Cache جدید
         */

        putUserCaches(updatedUser);

        return updatedUser;
    }


    // =========================================================
    // DELETE USER
    // =========================================================

    @Transactional
    @Override
    public Object deleteUserById(Long id) {

        User user = findUserById(id);

        String username = user.getUsername();
        String sub = user.getSub();
        String walletId = user.getWalletId();

        userRepository.delete(user);

        walletRMQProducer.deleteWalletBySub(walletId);

        /*
         * Delete all user caches
         */

        evictUserCaches(
                id,
                username,
                username,
                sub
        );

        return "کاربر با موفقیت حذف شد";
    }


    // =========================================================
    // AUTHENTICATION -> USER
    // =========================================================

    @Override
    public User convertUserFromAuthentication(
            Authentication authentication
    ) {

        if (authentication == null ||
                !authentication.isAuthenticated()) {

            throw new AppUnAuthorizedException(
                    "کاربر احراز هویت نشده است",
                    ""
            );
        }

        Object principal =
                authentication.getPrincipal();

        if (!(principal instanceof Jwt jwt)) {

            throw new AppUnAuthorizedException(
                    "توکن JWT معتبر نیست",
                    ""
            );
        }

        String sub = jwt.getSubject();

        if (sub == null || sub.isBlank()) {

            throw new AppUnAuthorizedException(
                    "sub در توکن وجود ندارد",
                    ""
            );
        }

        /*
         * مهم:
         *
         * اینجا مستقیماً repository را صدا نمی‌زنیم.
         *
         * findUserBySub دارای @Cacheable است.
         */

        return findUserBySub(sub);
    }


    // =========================================================
    // CACHE HELPERS
    // =========================================================

    private void putUserCaches(User user) {

        /*
         * این متد فقط برای توضیح ساختار است.
         *
         * CachePut روی متد public بهتر است انجام شود
         * و به خاطر Self Invocation نباید مستقیم annotation
         * روی همین متد private قرار بگیرد.
         */
    }


    private void evictUserCaches(
            Long id,
            String oldUsername,
            String newUsername,
            String sub
    ) {

        /*
         * این متد هم فقط placeholder است.
         *
         * برای invalidate واقعی، بهتر است CacheManager
         * را inject کنیم.
         */
    }
}