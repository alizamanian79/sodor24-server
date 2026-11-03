package com.app.server.service.impliment;

import com.app.server.dto.request.LoginRequestDto;
import com.app.server.dto.request.RegisterRequestDto;
import com.app.server.dto.request.users.ChangeAccountOtpRequestDto;
import com.app.server.dto.request.users.VerifyAccountRequestDto;
import com.app.server.dto.response.CustomResponseDto;
import com.app.server.dto.response.LoginResponseDto;
import com.app.server.exception.AppUnAuthorizedException;
import com.app.server.service.AuthenticationService;
import com.app.server.service.UserService;
import com.app.server.util.ExternalRequest.ExternalRequest;
import com.app.server.util.ExternalRequest.dto.ExternalRequestDto;
import com.app.server.util.ExternalRequest.dto.Method;
import com.app.server.util.notification_service.NotificationRequest;
import com.app.server.util.notification_service.NotificationService;
import com.app.server.util.notification_service.NotificationType;
import com.app.server.util.wallet_service_producer.WalletRMQProducer;
import com.app.server.util.wallet_service_producer.dto.request.ActivityRequestDto;
import com.app.server.util.wallet_service_producer.dto.request.CreateWalletRequestDto;
import com.app.server.util.wallet_service_producer.dto.response.WalletResponseDto;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private static final String REGISTER_PATH = "/api/v1/authentication/direct/register";
    private static final String LOGIN_PATH = "/api/v1/authentication/direct/login";
    private static final String VERIFY_OTP_PATH = "/api/v1/authentication/verify/otp";
    private static final String CHANGE_OTP_PATH = "/api/v1/authentication/change/otp";

    private static final String OTP_SENT_MSG = "کد تایید به شماره %s فرستاده شد";
    private static final String REGISTER_FAILED_MSG = "مشکل در ثبت نام . مجددا ثبت نام نمایید";
    private static final String LOGIN_SUCCESS_MSG = "ورود شما با موفقیت تایید شد";

    private final NotificationService notificationService;

    private static final int WORKER_THREADS = 8;
    private static final int QUEUE_CAPACITY = 1000;

    private final UserService userService;
    private final ExternalRequest externalRequest;

    @Value("${auth-service.base-url:http://localhost:8081}")
    private String baseUrl;

    /** Background workers + bounded queue for work the client does not wait for. */
    private ThreadPoolExecutor backgroundExecutor;

    private final WalletRMQProducer walletRMQProducer;


    @PostConstruct
    void initExecutor() {
        AtomicInteger counter = new AtomicInteger();
        backgroundExecutor = new ThreadPoolExecutor(
                WORKER_THREADS, WORKER_THREADS,
                60, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(QUEUE_CAPACITY),
                r -> new Thread(r, "auth-bg-" + counter.incrementAndGet()),
                new ThreadPoolExecutor.CallerRunsPolicy()); // queue full -> run in caller, never drop a task
        backgroundExecutor.allowCoreThreadTimeOut(true);
    }

    @PreDestroy
    void shutdownExecutor() {
        backgroundExecutor.shutdown();
        try {
            if (!backgroundExecutor.awaitTermination(10, TimeUnit.SECONDS)) {
                backgroundExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            backgroundExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    // ---------- public API ----------

    @Override
    public CustomResponseDto registerUser(RegisterRequestDto req) {
        Map<String, Object> res = post(REGISTER_PATH, req);
        Object otp = res.get("otp");

        if (otp == null) {
            return response(REGISTER_FAILED_MSG);
        }

        String phone = req.getPhoneNumber();
        runInBackground("send-otp-sms", () -> afterRegister(req, otp.toString()));

        return response(OTP_SENT_MSG.formatted(phone));
    }

    @Override
    public LoginResponseDto login(LoginRequestDto req) throws AppUnAuthorizedException {
        try {
            Map<String, Object> res = post(LOGIN_PATH, req);

            return LoginResponseDto.builder()
                    .message(LOGIN_SUCCESS_MSG)
                    .status(HttpStatus.OK)
                    .access_token(requireString(res, "access_token"))
                    .refresh_token(requireString(res, "refresh_token"))
                    .build();
        } catch (RuntimeException e) {
            throw new AppUnAuthorizedException(e.getMessage());
        }
    }

    @Override
    public Map<String, Object> verifyAccountOtp(VerifyAccountRequestDto req) {
        Map<String, Object> res = post(VERIFY_OTP_PATH, req);

        if (res.get("data") instanceof Map<?, ?> data
                && data.get("sub") instanceof String sub
                && !sub.isBlank()) {
            runInBackground("create-user", () -> afterVerifiedAccount(sub));
        }
        return res;
    }

    @Override
    public CustomResponseDto changeVerifiyAccountOtp(ChangeAccountOtpRequestDto req) {
        Map<String, Object> res = post(CHANGE_OTP_PATH, req);

        if (res.get("data") == null) {
            return response(REGISTER_FAILED_MSG);
        }
        return response(OTP_SENT_MSG.formatted(req.getPhoneNumber()));
    }


    // ---------- helpers ----------

    private void runInBackground(String taskName, Runnable task) {
        backgroundExecutor.execute(() -> {
            try {
                task.run();
            } catch (Exception e) {
                log.error("Background task '{}' failed", taskName, e);
            }
        });
    }

    private Map<String, Object> post(String path, Object body) {
        return externalRequest.sendRequest(
                ExternalRequestDto.builder()
                        .url(baseUrl + path)
                        .body(body)
                        .token(null)
                        .method(Method.POST)
                        .param(null)
                        .build());
    }

    private static String requireString(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) {
            throw new IllegalStateException("Missing field in auth response: " + key);
        }
        return value.toString();
    }

    private static String mask(String phone) {
        if (phone == null || phone.length() < 4) {
            return "****";
        }
        return "****" + phone.substring(phone.length() - 4);
    }

    private static CustomResponseDto response(String message) {
        return CustomResponseDto.builder()
                .message(message)
                .data(null)
                .status(HttpStatus.OK.value())
                .timestamp(LocalDateTime.now())
                .details(null)
                .build();
    }


    // ---------- background tasks ----------

    void afterRegister(RegisterRequestDto req, String otpCode) {
        // TODO: call the SMS provider here. Never log the OTP itself.
//        log.info("OTP SMS dispatched to {}", mask(req.));


        notificationService.notify(new NotificationRequest(
                NotificationType.EMAIL,
                req.getEmail(),
                "کد تأیید یکبار مصرف",
                "کاربر گرامی،\n\n"
                        + "کد تأیید یکبار مصرف شما برای ورود به حساب کاربری:\n\n"
                        + otpCode
                        + "\n\n"
                        + "این کد را در اختیار دیگران قرار ندهید."
        ));

    }

    void afterVerifiedAccount(String sub) {
        userService.createUser(sub);
        createWallet(sub);
    }



    public WalletResponseDto createWallet(String sub){
        CreateWalletRequestDto req = CreateWalletRequestDto.builder()
                .sub(sub)
                .currency("IRT")
                .balance(BigDecimal.ZERO)
                .build();
        WalletResponseDto res = walletRMQProducer.createWallet(req);
        ActivityRequestDto activeReq = ActivityRequestDto.builder()
                .sub(sub).value(true).build();
        walletRMQProducer.setActive(activeReq);

        return res;

    }

}