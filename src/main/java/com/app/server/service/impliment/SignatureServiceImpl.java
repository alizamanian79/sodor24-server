package com.app.server.service.impliment;

import com.app.server.dto.response.CustomResponseDto;
import com.app.server.exception.AppNotFoundException;
import com.app.server.model.Signature;
import com.app.server.model.User;
import com.app.server.repository.SignatureRepository;
import com.app.server.repository.UserRepository;
import com.app.server.service.SignatureService;
import com.app.server.service.UserService;
import com.github.mfathi91.time.PersianDate;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SignatureServiceImpl implements SignatureService {

    private final SignatureRepository signatureRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    @Value("${signature.price}")
    private Long signaturePrice;



    @Transactional
    @Override
    public Signature generateSignature(Long userId , Signature signature) {
        User user = userRepository.findById(userId).get();
        Signature generate = Signature.builder()
                .user(user)
                .signatureId(signature.getSignatureId())
                .expiredAt(signature.getExpiredAt())
                .isValid(true)
                .slug(signature.getSlug())
                .usageCount(signature.getUsageCount())
                .price(signaturePrice*signature.getUsageCount())
                .build();
      return signatureRepository.save(generate);
    }

    @Override
    public List<Signature> signatureList() {
        return signatureRepository.findAll();
    }

    @Override
    public Signature getSignatureById(Long signatureId) {
       Signature signature = signatureRepository.findSignatureById(signatureId).orElseThrow(()-> new AppNotFoundException("Signature not found"));
    return signature;
    }

    @Override
    public Object deleteSignatureById(Long signatureId) {

        Signature findSignature = signatureRepository.findSignatureById(signatureId).get();
        User findUser = userService.findUserById(findSignature.getUser().getId());

        findUser.getSignatures().remove(findSignature);
        signatureRepository.delete(findSignature);


        CustomResponseDto res = CustomResponseDto
                .builder()
                .message("امضا با موفقیت حذف شد")
                .details("امضا کاربر با موفقیت حذف شد")
                .timestamp(PersianDate.now())
                .build();
        return res;
    }


    @Override
    public Signature findSignatureByIdSlug(String slug) {
        Signature signatureExist = signatureRepository.findSignatureBySlug(slug).orElseThrow(()-> new AppNotFoundException("Signature not found"));
    return signatureExist;
    }

    @Transactional
    @Override
    public boolean useSignature(String signatureSlug , int count) {
        if (count==0){
            count=1;
        }
        CustomResponseDto res = new CustomResponseDto();
        Signature findSignature = findSignatureByIdSlug(signatureSlug);
        if (findSignature.getUsageCount() < count || findSignature.getUsageCount() <=0) {
            res.setMessage("مقدار استفاده شما از امضای خود به پایان رسید");
            res.setDetails(findSignature.getUsageCount() < count ? "مقدار استفاده از تعداد امضا کمتر میباشد" :"لطفا برای خرید امضا مراجعه کنید");
            res.setTimestamp(PersianDate.now());
            res.setStatus(HttpStatus.OK.value());
            findSignature.setValid(false);
            signatureRepository.save(findSignature);
            return false;
        }else {
            findSignature.setUsageCount(findSignature.getUsageCount() - count);
            signatureRepository.save(findSignature);
            res.setMessage(findSignature.getUsageCount()+"با موفقیت امضا تایید شد. مقدار فعلی امضای شما: ");
            res.setDetails("");
            res.setTimestamp(PersianDate.now());
            res.setStatus(HttpStatus.OK.value());
            return true;
        }


    }



    @Scheduled(cron = "*/10 * * * * *")
    public void cleanExpiredSignatures() {
        LocalDateTime now = LocalDateTime.now();

       List<Signature> signatures = signatureRepository.findSignatureByExpiredAtBefore(now);
        signatures.stream().map(item->{
            item.setValid(false);
            signatureRepository.save(item);
            return item;
        }).collect(Collectors.toSet());

    }

}
