package com.app.server.service.impliment.OtpFactory;

import com.app.server.dto.response.Sodor24ResponseDto;
import com.app.server.exception.AppBadRequestException;
import com.app.server.exception.AppForbiddenException;
import com.app.server.exception.AppNotFoundException;
import com.app.server.model.NotificationType;
import com.app.server.model.Otp;
import com.app.server.model.Signature;
import com.app.server.model.User;
import com.app.server.repository.OtpRepository;
import com.app.server.repository.SignatureRepository;
import com.app.server.service.NotificationService;
import com.app.server.service.OtpService;
import com.app.server.service.SignatureService;
import com.app.server.service.impliment.NotificationFactory;
import com.app.server.service.impliment.RandomCodeGenerator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class SignatureOtpServiceImpl implements OtpService {

    private final OtpRepository otpRepository;
    private final SignatureRepository signatureRepository;
    private final RandomCodeGenerator randomcode;
    private final NotificationFactory notificationFactory;


    CompletableFuture<String> generateCode(){
        return CompletableFuture.supplyAsync(()->randomcode.generate(5));
    }

    CompletableFuture<Otp> getOtpCode(String code){
        return CompletableFuture.supplyAsync(()->
                otpRepository.findOtpByCode(code)
                        .orElseThrow(()->new AppNotFoundException("کد شما اشتباه میباشد")));
    }

    CompletableFuture<Signature> getSignatureByOtp(String code){
        return CompletableFuture.supplyAsync(()->{
            return signatureRepository.findByOtp(code).orElseThrow(()->
                    new AppNotFoundException("کد تایید اشتباه است"));
        });
    }


    CompletableFuture<Signature> getSignatureById(String id){
       Long sid = Long.parseLong(id);
        return CompletableFuture.supplyAsync(()->
             signatureRepository.findById(sid).orElseThrow(()->
                    new AppNotFoundException("امضا پیدا نشد"))
        );
    }


    @Override
    public String generateOtp(String receiver) {

        System.out.println("signature_id->\s" + receiver);

        // check otp exist or not
        Optional<Otp> hasOtp = otpRepository.findOtpByReceiver(receiver);

        if (hasOtp.isPresent()){
            throw new AppBadRequestException("کد تاییده به فرستنده ارسال شده است . لطفا بعد از "
                    +timeToExpired(hasOtp.get().getExpiresAt())+
                    " دقیقه مجددا اقدام نمایید");
        }

        CompletableFuture<String> generateCodeFuture=generateCode();
        CompletableFuture<Signature> findSignature = getSignatureById(receiver);
        CompletableFuture.allOf(generateCodeFuture,findSignature);





        String code = generateCodeFuture.join();
        Signature signature = findSignature.join();



        // Otp builder
        Otp otpBuilder = Otp.builder()
                .code(code)
                .receiver(receiver)
                .expiresAt(LocalDateTime.now().plusMinutes(2))
                .build();
        otpRepository.save(otpBuilder);



        // update signature
        signature.setOtp(code);
        signature.setVerified(false);
        signature.setValid(false);
        signatureRepository.save(signature);



        NotificationService service = notificationFactory.getService(NotificationType.SMS);
        service.sendNotification(signature.getUser().getPhoneNumber(),
                "در خواست شما برای امضای " +
                       "\s"+ signature.getSignaturePlan().getTitle()+"\s" +
                        " به مبلغ " +
                        "\s" +signature.getTotalPrice()+"\s"+
                        " تومان ثبت گردید." +
                        "کد تایید شما: " +
                        "\s" +code+
                        "\s"
                );

        return "کد ارسال شد";
    }

    @Override
    public Object verifyOtp(String receiver, String code, Object data) {

        System.out.println("reviever -> \s" + receiver);
        System.out.println("code -> \s" + code);

        CompletableFuture<Signature> signatureFuture= getSignatureById(receiver);
        CompletableFuture<Otp> getOtpFuture = getOtpCode(code);

        CompletableFuture.allOf(signatureFuture,getOtpFuture);

        Signature signature = signatureFuture.join();
        Otp otpCode = getOtpFuture.join();


        if (!signature.getOtp().equals(otpCode.getCode())){
            throw new AppForbiddenException("کد شما اشتباه است");
        }

        signature.setOtp(null);
        signature.setValid(false);
        signature.setVerified(true);
        signature.setStatus("در انتظار پرداخت");
        signatureRepository.save(signature);
        otpRepository.delete(otpCode);


        return signature.isValid();
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




    @Scheduled(fixedRate = 120_000)
    @Transactional
    public void removeExpiredOtps() {
        LocalDateTime now = LocalDateTime.now();
        List<Otp> expiredOtps = otpRepository.findByExpiresAtBefore(now);

        for (Otp otp : expiredOtps) {
            Signature signature = signatureRepository.findSignatureByOtp(otp.getCode()).get();

            if (signature != null) {
                signature.setOtp(null);
                signature.setValid(false);
                signature.setVerified(false);
                signatureRepository.save(signature);

            }
        }

        long deleted = otpRepository.deleteByExpiresAtBefore(now);

        log.info("Deleted {} expired OTPs", deleted);
    }


    // deleted after 24 hours
    @Scheduled(fixedRate = 86400_000)
    @Transactional
    public void deletedSignatureExpired() {

        List<Signature> signatures = signatureRepository.findSignatureByVerified(false);

        for (Signature signature : signatures) {
            log.info("Deleted signature id-> \s", signature.getId());
            signatureRepository.deleteSignatureById(signature.getId());
        }

    }



}
