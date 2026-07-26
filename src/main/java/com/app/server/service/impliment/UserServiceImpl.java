package com.app.server.service.impliment;

import com.app.server.config.RedisHealthChecker;
import com.app.server.dto.request.RegisterRequestDto;
import com.app.server.dto.request.UpdateUserRequestDto;
import com.app.server.dto.response.RegisterResponseDto;
import com.app.server.dto.response.Sodor24ResponseDto;
import com.app.server.exception.AppNotFoundException;
import com.app.server.exception.AppUnAuthorizedException;
import com.app.server.model.NotificationType;
import com.app.server.model.Role;
import com.app.server.model.User;
import com.app.server.repository.UserRepository;
import com.app.server.service.NotificationService;
import com.app.server.service.UserService;
import com.app.server.util.wallet_service_producer.WalletRMQProducer;
import com.app.server.util.wallet_service_producer.dto.request.CreateWalletRequestDto;
import com.app.server.util.wallet_service_producer.dto.response.WalletResponseDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    @Value("${application.wallet-service.currency}")
    private String currency;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RedisHealthChecker redisHealthChecker;
    private final WalletRMQProducer walletRMQProducer;
    private final NotificationFactory notificationFactory;
    private final OtpService otpService;


    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll(Sort.by("id").ascending());
    }


    CompletableFuture<User> createUser(RegisterRequestDto req){
        return CompletableFuture.supplyAsync(()->{
            User user = User.builder()
                    .username(req.getUsername())
                    .password(passwordEncoder.encode(req.getPassword()))
                    .fullName(req.getFullName())
                    .phoneNumber(req.getPhoneNumber())
                    .roles(Set.of(Role.USER))
                    .walletId(null)
                    .build();
            return userRepository.save(user);
        });
    }

    CompletableFuture<String> createWalletSub(){
        return CompletableFuture.supplyAsync(()->createWallet());
    }

    @Transactional
    @Override
    public RegisterResponseDto registerUser(RegisterRequestDto req) {

        // Register user
        CompletableFuture<User> createUserFuture=createUser(req);
        CompletableFuture<String> createWalletFuture=createWalletSub();

        CompletableFuture.allOf(createUserFuture,createWalletFuture).join();
        User user = createUserFuture.join();
        String walletId = createWalletFuture.join();
        user.setWalletId(walletId);



        NotificationService service = notificationFactory.getService(NotificationType.SMS);
        service.sendNotification(user.getPhoneNumber(),"به صدور24 خوش آمدید.\n" +
                "ثبت\u200Cنام شما با موفقیت انجام شد.\n" +
                "کد تأیید شما:" +"\s"+otpService.generateAndSend(user.getPhoneNumber())+"\s"+"\n"+
                "از همراهی شما سپاسگزاریم.");


        return RegisterResponseDto.builder()
                .message("با موفقیت ایجاد شد " + user.getUsername() + " کاربر")
                .status(HttpStatus.CREATED.value())
                .details("خوش آمدید")
                .timestamp(new Date())
                .build();
    }

    @Override
    public User findUserByUsername(String username) {

        if (redisHealthChecker.isRedisAvailable()) {
            return findUserByUsernameCached(username);
        } else {
            return userRepository.findUserByUsername(username)
                    .orElseThrow(() -> new AppUnAuthorizedException(
                            "کاربر با این نام کاربری پیدا نشد",
                            ""));
        }
    }


    @Cacheable(value = "userByUsername", key = "#username")
    public User findUserByUsernameCached(String username) {
        return userRepository.findUserByUsername(username)
                .orElseThrow(() -> new AppUnAuthorizedException(
                        "کاربر با این نام کاربری پیدا نشد",
                        "لیست کاربران را مجدد بررسی نمایید"));
    }



    @Override
    public Boolean existUserByUsername(String username) {
        return userRepository.findUserByUsername(username).isPresent();
    }

    @Override
    @Cacheable(value = "userById", key = "#id")
    public User findUserById(Long id) {
        return userRepository.findUserById(id)
                .orElseThrow(() -> new AppUnAuthorizedException(
                        "کاربری با این آیدی پیدا نشد",
                        "لیست کاربران را مجدد بررسی نمایید"
                ));
    }

    @Cacheable(value = "userByPhoneNumber",key = "#phoneNumber")
    @Override
    public User findUserByPhoneNumber(String phoneNumber) {
        return userRepository.findUserByPhoneNumber(phoneNumber).orElseThrow(()->new AppNotFoundException("شماره تماس شما اشتباه میباشد"));
    }


    @Override
    @Transactional
    @CachePut(value = "userById", key = "#id")
    public User updateUser(UpdateUserRequestDto req, Long id) {
        User existUser = findUserById(id);
        existUser.setUsername(req.getUsername());
        existUser.setPassword(passwordEncoder.encode(req.getPassword()));
        existUser.setFullName(req.getFullName());
        existUser.setPhoneNumber(req.getPhoneNumber());
        existUser.setRoles(existUser.getRoles());
        clearAllUserCache();
        return userRepository.save(existUser);
    }

    @Transactional
    @Override
    public User changeUserRole(Long id, Set<Role> roles) {
        User existUser = findUserById(id);
        existUser.setRoles(roles);

        User savedUser = userRepository.save(existUser);

        if (redisHealthChecker.isRedisAvailable()) {
            updateUserCache(savedUser);
        }

        return savedUser;
    }

    @CachePut(value = "userById", key = "#user.id")
    public User updateUserCache(User user) {
        return user;
    }


    @Override
    @CacheEvict(value = { "userById", "userByUsername" }, key = "#id")
    public Object deleteUserById(Long id) {
        User user = findUserById(id);
        userRepository.delete(user);
        clearAllUserCache();

        return Sodor24ResponseDto.response(
                "",
                    "کاربر با موفقیت حذف شد"
                ,""
                ,"",
                HttpStatus.OK
        );


    }

    @CacheEvict(value = { "users" }, allEntries = true)
    public void clearAllUserCache() {
        System.out.println("Clearing all users cache...");
    }

    @Override
    public User convertUserFromAuthentication(Authentication auth){
        User user = (User) auth.getPrincipal();
        if (user==null) {
            return null;
        }
        return user;
    }

    public String createWallet(){
        CreateWalletRequestDto req = CreateWalletRequestDto.builder()
                .sub("")
                .balance(BigDecimal.ZERO)
                .currency(currency)
                .build();
        WalletResponseDto res = walletRMQProducer.createWallet(req);
        Map<String,Object> data = (Map<String, Object>) res.getData();
        String sub = data.get("sub").toString();
        return sub;
    }






}
