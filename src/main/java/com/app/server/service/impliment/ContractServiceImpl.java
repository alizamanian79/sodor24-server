package com.app.server.service.impliment;

import com.app.server.dto.request.ContractRequestDto;
import com.app.server.exception.AppBadRequestException;
import com.app.server.exception.AppNotFoundException;
import com.app.server.model.Contract;
import com.app.server.model.Signature;
import com.app.server.model.User;
import com.app.server.model.UserContract;
import com.app.server.repository.ContractRepository;
import com.app.server.repository.UserContractRepository;
import com.app.server.service.ContractService;
import com.app.server.service.SignatureService;
import com.app.server.service.UserService;
import com.app.server.util.rabbitMQ.ContractRMQProducer;
import com.app.server.util.rabbitMQ.dto.request.RMQContractRequestDto;
import com.app.server.util.rabbitMQ.dto.response.RMQContractResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;


@Service
@RequiredArgsConstructor
public class ContractServiceImpl implements ContractService {

    private final ContractRepository contractRepository;
    private final UserService userService;
    private final UserContractRepository userContractRepository;
    private final SignatureService signatureService;
    private final  ContractRMQProducer contractProducer;


    @Override
    public List<Contract> contractList() {
        List<Contract> contracts = contractRepository.findAll();
        Collections.reverse(contracts);
        return contracts;
    }

    @Transactional
    @Override
    public Contract preparationContract(ContractRequestDto req) throws Exception {

        Contract contract=new Contract();
        Signature signature = checkSignature(req.getSignatureId());

        if (req.getSlug().isBlank()){
            contract = signingNewContract(req);
        }else {
            contract = signingContractExist(req);
        }


       String exName = contract.getSlug().toString()+".pdf";
        MultipartFile mainFile = renameMultipartFile(req.getPdfFile(),exName);

        RMQContractRequestDto result = RMQContractRequestDto.builder()
                .file(mainFile)
                .privateKeyFile(req.getPrivateKeyFile())
                .keyPassword(req.getPassword())
                .country(signature.getCountry())
                .reason(signature.getReason())
                .build();
        RMQContractResponse res = sendAndReceive(result);
        System.out.println(res);
        contract.setSignedLink(res.getData().getSignedPdf());
        contract.setUnSignedLink(res.getData().getUnsignedPdf());
        contractRepository.save(contract);
        return contract;

    }



    @Override
    public Contract findContractById(Long contractId) {
        Optional<Contract> find = Optional.of(contractRepository.findById(contractId).orElseThrow(() -> new AppNotFoundException("قرارداد با ایدی مورد نظر شما پیدا نشد")));
        return find.get();
    }

    @Override
    public Contract findContractBySlug(String slug) {
        Optional<Contract> find = Optional.of(contractRepository.findContractBySlug(slug).orElseThrow(() -> new AppNotFoundException("قرارداد با ایدی مورد نظر شما پیدا نشد")));
        return find.get();
    }

    @Override
    public String deleteContractById(Long id) {
        Contract findContract = findContractById(id);
        contractRepository.delete(findContract);
        return "قرارداد شما با موفقیت حذف شد";
    }

    @Override
    public boolean isExistContract(String slug) {
        boolean find = contractRepository.existsBySlug(slug);
        return find;
    }



    // Generate slug
    public String createSlug() {
        Random random = new Random();
        StringBuilder result = new StringBuilder(5);

        for (int i = 0; i < 5; i++) {
            int digit = random.nextInt(10); // 0-9
            result.append(digit);
        }

        return result.toString();
    }




    // Main part

    @Transactional
    public Signature checkSignature(Long signatureId) throws AppBadRequestException{
        Signature exitSignature = signatureService.findById(signatureId);
        if (!exitSignature.isValid()){
            throw new AppBadRequestException("امضای شما معتبر نمیباشد","به پنل مراجعه کرده و تاریخ و تعداد استفاده رو برسی نمایید");
        }

         else if (exitSignature.getUsageCount()<=0){
            throw new AppBadRequestException("تعداد امضای شما تمام شده");
        }

        else if (exitSignature.getSignatureExpired().isBefore(LocalDateTime.now())){
            throw new AppBadRequestException("تاریخ امضای شما تمام شده");
        }

        exitSignature.setUsageCount(exitSignature.getUsageCount()-1);
        return signatureService.updateSignatureIntenral(exitSignature);

    }

    // Send to rabbitmq contract
    public RMQContractResponse sendAndReceive(RMQContractRequestDto req) throws Exception{
        try{
            return contractProducer.sendAndReceive(req);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // New contract and signer
    public Contract signingNewContract(ContractRequestDto req){

        // Find user
        User existUser = userService.findUserById(req.getUserId());
        // Preparation contract and save
        Contract contract = Contract.builder()
                .title(req.getTitle())
                .description(req.getDescription())
                .slug(createSlug().toString())
                .signedLink(req.getSignedLink())
                .unSignedLink(req.getUnSignedLink())
                .signers(List.of())
//                .createdBy(existUser)
                .build();
        contractRepository.save(contract);

        // Put it in user contract join table and save
        UserContract userContract = UserContract.builder()
                .user(existUser)
                .signature(signatureService.findById(req.getSignatureId()))
                .contract(contract)
                .build();
        userContractRepository.save(userContract);
        return contract;
    }

    // Contract is exist and new signer want sign
    public Contract signingContractExist(ContractRequestDto req){

        // Find Contract
        Contract exitContract = findContractBySlug(req.getSlug());
        User exitUser = userService.findUserById(req.getUserId());

        UserContract signingBuilder= UserContract.builder()
                .user(exitUser)
                .contract(exitContract)
                .build();
        userContractRepository.save(signingBuilder);
        return signingBuilder.getContract();

    }

    public MultipartFile renameMultipartFile(MultipartFile file, String newFileName) throws IOException {
        return new MockMultipartFile(
                newFileName,                // New filename
                newFileName,                // Original filename (optional)
                file.getContentType(),      // Content type
                file.getInputStream()       // File content
        );
    }

}
