package com.app.server.service.impliment.OtpFactory;

import com.app.server.dto.response.Sodor24ResponseDto;
import com.app.server.exception.AppBadRequestException;
import com.app.server.exception.AppNotFoundException;
import com.app.server.model.NotificationType;
import com.app.server.model.Otp;
import com.app.server.model.User;
import com.app.server.repository.OtpRepository;
import com.app.server.repository.UserRepository;
import com.app.server.service.NotificationService;
import com.app.server.service.OtpService;
import com.app.server.service.impliment.NotificationFactory;
import com.app.server.service.impliment.RandomCodeGenerator;
import com.app.server.util.wallet_service_producer.WalletRMQProducer;
import com.app.server.util.wallet_service_producer.dto.request.ActivityRequestDto;
import com.app.server.util.wallet_service_producer.dto.request.CreateWalletRequestDto;
import com.app.server.util.wallet_service_producer.dto.response.WalletResponseDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserOtpServiceImpl implements OtpService {

    @Value("${application.wallet-service.currency}")
    public String currency;

    private final OtpRepository otpRepository;
    private final NotificationFactory notificationFactory;
    private final WalletRMQProducer walletRMQProducer;
    private final RandomCodeGenerator randomcode;
    private final UserRepository userRepository;





    public String createWallet(){
        CreateWalletRequestDto req = CreateWalletRequestDto.builder()
                .sub("")
                .balance(BigDecimal.ZERO)
                .currency(currency)
                .build();

        WalletResponseDto res = walletRMQProducer.createWallet(req);
        Map<String,Object> data = (Map<String, Object>) res.getData();
        String sub = data.get("sub").toString();

        ActivityRequestDto actReq= ActivityRequestDto.builder()
                .sub(sub)
                .value(true)
                .build();

        walletRMQProducer.setActive(actReq);


        return sub;
    }

    CompletableFuture<String> createWalletSub(){
        return CompletableFuture.supplyAsync(()->createWallet());
    }



    CompletableFuture<Otp> getOtpCode(String code){
        return CompletableFuture.supplyAsync(()->
                otpRepository.findOtpByCode(code)
                        .orElseThrow(()->new AppNotFoundException("کد شما اشتباه میباشد")));

    }

    CompletableFuture<User> getUserByPhoneNumber(String phoneNumber){
        return CompletableFuture.supplyAsync(()->
                userRepository.findUserByPhoneNumber(phoneNumber).orElseThrow(()->
                        new AppNotFoundException("کاربر با این شماره تماس پیدا نشد")));
    }


    CompletableFuture<String> generateCode(){
        return CompletableFuture.supplyAsync(()->randomcode.generate(5));
    }


    @Transactional
    @Override
    public String generateOtp(String phoneNumber) {

        // check otp exist or not
       Optional<Otp> hasOtp = otpRepository.findOtpByReceiver(phoneNumber);
       if (hasOtp.isPresent()){
           throw new AppBadRequestException("کد تاییده به فرستنده ارسال شده است . لطفا بعد از "
                    +timeToExpired(hasOtp.get().getExpiresAt())+
                   " دقیقه مجددا اقدام نمایید");
       }


        CompletableFuture<User> userFuture = getUserByPhoneNumber(phoneNumber.toString());
        CompletableFuture<String> generateCodeFuture=generateCode();
        CompletableFuture.allOf(generateCodeFuture,userFuture);

        User user = userFuture.join();
        String code = generateCodeFuture.join();

        // Otp builder
        Otp otpBuilder = Otp.builder()
                .code(code)
                .receiver(phoneNumber)
                .expiresAt(LocalDateTime.now().plusMinutes(2))
                .build();
        otpRepository.save(otpBuilder);


        user.setOtp(code);
        user.setValid(false);
        userRepository.save(user);


        NotificationService service = notificationFactory.getService(NotificationType.SMS);
        service.sendNotification(user.getPhoneNumber(),"به صدور24 خوش آمدید.\n" +
                "ثبت نام شما با موفقیت انجام شد.\n" +
                "کد تأیید شما:" +"\s"+code+"\s"+"\n"+
                "از همراهی شما سپاسگزاریم.");

        return code;

    }

    @Override
    public Object verifyOtp(String receiver,String code,Object data) {


        CompletableFuture<User> userFuture=getUserByPhoneNumber(receiver);
        CompletableFuture<Otp> otpFuture = getOtpCode(code);
        CompletableFuture<String> subWallet = createWalletSub();
        CompletableFuture.allOf(userFuture,otpFuture);

        User user = userFuture.join();
        Otp otp= otpFuture.join();
        String wallet= subWallet.join();

        user.setValid(true);
        user.setOtp(null);
        user.setWalletId(wallet);
        userRepository.save(user);
        otpRepository.delete(otp);



      return user.isValid();
    }


    @Scheduled(fixedRate = 120000)
    @Transactional
    public void removeExpiredOtps() {
        LocalDateTime now = LocalDateTime.now();
        List<Otp> expiredOtps = otpRepository.findByExpiresAtBefore(now);

        for (Otp otp : expiredOtps) {
            User user = userRepository.findUserByOtp(otp.getCode()).get();

            if (user != null) {
                user.setOtp(null);
                user.setValid(false);
                walletRMQProducer.deleteWalletBySub(user.getWalletId());
                user.setWalletId(null);
                userRepository.save(user);

            }
        }

        long deleted = otpRepository.deleteByExpiresAtBefore(now);

        log.info("Deleted {} expired OTPs", deleted);
    }


    public String timeToExpired(LocalDateTime expiredTime) {

        Duration remaining = Duration.between(
                LocalDateTime.now(),
                expiredTime
        );

        if (remaining.isNegative()) {
            return "Expired";
        }

        long minutes = remaining.toMinutes();
        long seconds = remaining.minusMinutes(minutes).getSeconds();

        return String.format("%02d:%02d", minutes, seconds);
    }


}
