package com.app.server.service.impliment;

import com.app.server.config.RedisHealthChecker;
import com.app.server.dto.request.RegisterRequestDto;
import com.app.server.dto.request.UpdateUserRequestDto;
import com.app.server.dto.response.RegisterResponseDto;
import com.app.server.exception.AppNotFoundException;
import com.app.server.exception.AppUnAuthorizedException;
import com.app.server.model.OtpType;
import com.app.server.model.Role;
import com.app.server.model.User;
import com.app.server.repository.UserRepository;
import com.app.server.service.OtpService;
import com.app.server.service.UserService;
import com.app.server.service.impliment.OtpFactory.OtpFactory;
import com.app.server.util.wallet_service_producer.WalletRMQProducer;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
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


    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RedisHealthChecker redisHealthChecker;
    private final WalletRMQProducer walletRMQProducer;
    private final OtpFactory otpFactory;


    @Override
    public List<User> getAllUsers() {
        clearAllUserCache();
        return userRepository.findAll(Sort.by("id").ascending());
    }



    CompletableFuture<User> createUser(RegisterRequestDto req){
        return CompletableFuture.supplyAsync(()->{
            User user = User.builder()
                    .username(req.getUsername())
                    .password(passwordEncoder.encode(req.getPassword()))

                    .sub(req.getSub())
                    .firstName(req.getFirstName())
                    .lastName(req.getLastName())
                    .nationalCode(req.getNationalCode())
                    .email(req.getEmail())

                    .phoneNumber(req.getPhoneNumber())
//                    .roles(Set.of(Role.USER))
                    .walletId(null)
                    .build();
            return userRepository.save(user);
        });
    }





    @jakarta.transaction.Transactional(rollbackOn = Exception.class)
    @Override
    public RegisterResponseDto registerUser(RegisterRequestDto req) {

        // Register user
        CompletableFuture<User> createUserFuture=createUser(req);
        User user = createUserFuture.join();
        userRepository.save(user);

        OtpService otpService = otpFactory.getService(OtpType.USER);
        otpService.generateOtp(user.getPhoneNumber());


//        NotificationService service = notificationFactory.getService(NotificationType.SMS);
//        service.sendNotification(user.getPhoneNumber(),"به صدور24 خوش آمدید.\n" +
//                "ثبت\u200Cنام شما با موفقیت انجام شد.\n" +
//                "کد تأیید شما:" +"\s"+otpService.generateAndSend(user.getPhoneNumber())+"\s"+"\n"+
//                "از همراهی شما سپاسگزاریم.");
//

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
    public User findUserBySub(String sub){
        User exist = userRepository.findUserBySub(sub).orElseThrow(() -> new AppUnAuthorizedException(
                "کاربر با این sub پیدا نشد",
                ""));;
        return exist;
    }


    @Override
    @Transactional
    @CachePut(value = "userById", key = "#id")
    public User updateUser(UpdateUserRequestDto req, Long id) {
        User existUser = findUserById(id);
        existUser.setFirstName(req.getFirstName());
        existUser.setLastName(req.getLastName());
        existUser.setEmail(req.getEmail());
        existUser.setPhoneNumber(req.getPhoneNumber());
        existUser.setPassword(passwordEncoder.encode(req.getPassword()));
        existUser.setNationalCode(req.getNationalCode());
//        existUser.setRoles(existUser.getRoles());
        clearAllUserCache();
        return userRepository.save(existUser);
    }

//    @Transactional
//    @Override
//    public User changeUserRole(Long id, Set<Role> roles) {
//        User existUser = findUserById(id);
//        existUser.setRoles(roles);
//
//        User savedUser = userRepository.save(existUser);
//
//        if (redisHealthChecker.isRedisAvailable()) {
//            updateUserCache(savedUser);
//        }
//
//        return savedUser;
//    }

    @CachePut(value = "userById", key = "#user.id")
    public User updateUserCache(User user) {
        return user;
    }


    @Override
    @CacheEvict(value = { "userById", "userByUsername" }, key = "#id")
    public Object deleteUserById(Long id) {
        User user = findUserById(id);
        String sub = user.getWalletId();
        userRepository.delete(user);
        clearAllUserCache();
        walletRMQProducer.deleteWalletBySub(sub);
        return"کاربر با موفقیت حذف شد";


    }

    @CacheEvict(value = { "users" }, allEntries = true)
    public void clearAllUserCache() {
        System.out.println("Clearing all users cache...");
    }

    @Override
    public User convertUserFromAuthentication(Authentication authentication) {

        Jwt jwt = (Jwt) authentication.getPrincipal();
        String sub = jwt.getSubject();
        return userRepository.findUserBySub(sub)
                .orElseThrow(() -> new RuntimeException(
                        "User not found with sub: " + sub
                ));
    }






}
