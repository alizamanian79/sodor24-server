package com.app.server.service.impliment;

import com.app.server.dto.request.ContractRequestDto;
import com.app.server.exception.AppNotFoundException;
import com.app.server.model.Contract;
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
import org.springframework.stereotype.Service;

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
    public Contract preparationContract(ContractRequestDto req) {

        // Find user
        User existUser = userService.findUserById(req.getUserId());
        // Preparation contract and save
        Contract contract = Contract.builder()
                .title(req.getTitle())
                .description(req.getDescription())
                .slug(req.getSlug().isBlank()? createSlug() :req.getSlug())
                .signedLink(req.getSignedLink())
                .unSignedLink(req.getUnSignedLink())
                .signers(List.of())
//                .createdBy(existUser)
                .build();
        contractRepository.save(contract);
        // Put it in user contract join table and save

        UserContract userContract = UserContract.builder()
                .user(existUser)
                .signature(signatureService.findById(1L))
                .contract(contract)
        .build();
        userContractRepository.save(userContract);


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

    // Send to rabbitmq contract
    public RMQContractResponse sendAndReceive(RMQContractRequestDto req) throws Exception{
        try{
            return contractProducer.sendAndReceive(req);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
