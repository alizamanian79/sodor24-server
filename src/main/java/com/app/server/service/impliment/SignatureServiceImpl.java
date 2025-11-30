package com.app.server.service.impliment;

import com.app.server.dto.request.SignatureRequestDto;
import com.app.server.dto.response.CustomResponseDto;
import com.app.server.exception.AppNotFoundException;
import com.app.server.model.Signature;
import com.app.server.model.User;
import com.app.server.repository.SignatureRepository;
import com.app.server.repository.UserRepository;
import com.app.server.service.SignatureService;
import com.app.server.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mfathi91.time.PersianDate;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    private final RestTemplate restTemplate;


    public Signature saveSignature(Long userId , Signature signature) {
        User user = userRepository.findById(userId).get();
        Signature generate = Signature.builder()
                .user(user)
                .signatureId(signature.getSignatureId())
                .expiredAt(signature.getExpiredAt())
                .isValid(signature.isValid())
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
    public Object generateSignature(SignatureRequestDto req) throws JsonProcessingException {

        User user =userService.findUserByUsername(req.getUsername());
        req.setUsername(user.getFullName());

        // Send Request Signature
        Map<String,String> res = new HashMap<>();
        if (req.isValid()){
            res = sendSignatureRequest(req);
        }

        // Saving signature
        Signature signature = Signature.builder()
                .signatureId(res.get("signatureId"))
                .user(user)
                .expiredAt(LocalDateTime.now().plusDays(Integer.parseInt(req.getSignatureExpired())))
                .usageCount(req.getUsageCount())
                .isValid(req.isValid())
                .build();
        Signature saved = saveSignature(user.getId(),signature);
        Map<String, Object> customRes = new HashMap<>();

        customRes.put("signatureInfo", saved);
        customRes.put("cert", res.get("cert"));
        customRes.put("private_key", res.get("public_key"));
        customRes.put("public_key", res.get("private_key"));
        customRes.put("fullName", res.get("fullName"));
        return customRes;
    }




    @Transactional
    @Override
    public boolean useSignature(String signatureSlug , int count) {


        if (count==0){
            count=1;
        }
        CustomResponseDto res = new CustomResponseDto();
        Signature findSignature = findSignatureByIdSlug(signatureSlug);

        if (!findSignature.isValid()){
            return false;
        }

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



    @Transactional
    @Override
    public boolean activeSignature(String slug) throws JsonProcessingException {
        Signature signature = findSignatureByIdSlug(slug);

        SignatureRequestDto req = SignatureRequestDto.builder()
                .username(signature.getUser().getFullName())
                .country("Iran")
                .reason("anything")
                .location("tehran")
                .organization("alibaba")
                .department("shiba")
                .state("UK")
                .city("iran")
                .email("alidev@gmail.com")
                .title("sad")
                .signatureExpired(String.valueOf(10))
                .signaturePassword("55880022")
                .usageCount(10)
                .valid(signature.isValid())
                .build();

        Map<String, String> res = sendSignatureRequest(req);


        signature.setSignatureId(res.get("signatureId"));
        signature.setValid(true);
        signatureRepository.save(signature);

        return true;
    }



    @Transactional
    @Override
    public Signature chargeSignature(String slug){
       Signature findSignature =  findSignatureByIdSlug(slug);
       findSignature.setUsageCount(findSignature.getUsageCount());
       findSignature.setCreatedAt(LocalDateTime.now());
       findSignature.setUsageCount(findSignature.getTotalUsageCount());
       findSignature.setTotalUsageCount(findSignature.getTotalUsageCount());
       findSignature.setExpiredAt(findSignature.getExpiredAt());
       findSignature.setValid(true);
       return signatureRepository.save(findSignature);
    }


    // Sending to signature microservice
    public Map<String,String> sendSignatureRequest(SignatureRequestDto req) throws JsonProcessingException {

        String idGenerator="signature-"+req.getUsername()+"-"+ UUID.randomUUID().toString();
        String url = "http://localhost:8585/api/v1/signature/generate";
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("username", req.getUsername() !=null ? req.getUsername(): "میهمان");
        requestBody.put("country", req.getCountry());
        requestBody.put("reason", req.getReason());
        requestBody.put("location", req.getLocation());
        requestBody.put("organization", req.getOrganization());
        requestBody.put("department", req.getDepartment());
        requestBody.put("state", req.getState());
        requestBody.put("city", req.getCity());
        requestBody.put("email", req.getEmail());
        requestBody.put("title", req.getTitle());
        requestBody.put("userId",idGenerator);
        requestBody.put("signatureExpired", Integer.parseInt(req.getSignatureExpired()));
        requestBody.put("signaturePassword", req.getSignaturePassword());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> responseBody = mapper.readValue(response.getBody(), Map.class);
        String id = (String) responseBody.get("userId");

        Map<String, Object> responseBodySignature = mapper.readValue(response.getBody(), Map.class);
        String cert = (String) responseBody.get("cert");
        String private_key = (String) responseBody.get("privateKey");
        String public_key = (String) responseBody.get("publicKey");
        String fullName = (String) responseBody.get("username");

        Map<String, String> customRes = new HashMap<>();
        customRes.put("cert", cert);
        customRes.put("private_key", private_key);
        customRes.put("public_key", public_key);
        customRes.put("fullName", fullName);
        return customRes;
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
