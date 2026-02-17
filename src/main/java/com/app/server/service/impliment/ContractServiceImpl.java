package com.app.server.service.impliment;

import com.app.server.dto.request.ContractRequestDto;
import com.app.server.exception.AppBadRequestException;
import com.app.server.model.Contract;
import com.app.server.model.User;
import com.app.server.model.UserContract;
import com.app.server.repository.ContractRepository;
import com.app.server.service.ContractService;
import com.app.server.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class ContractServiceImpl implements ContractService {

    private final ContractRepository contractRepository;
    private final UserService userService;
     UserContractServiceImpl userContractServiceImpl;

    @Override
    public List<Contract> contractList() {
        return contractRepository.findAll();
    }

    @Override
    public Contract contractPreparation(ContractRequestDto req) {
        User existUser = userService.findUserById(req.getUserId());
        Contract contract = Contract.builder()
                .owner(existUser)
                .title(req.getTitle())
                .description(req.getDescription())
                .pdf(req.getPdf())
                .signedLink(req.getSignedLink())
                .unSignedLink(req.getUnSignedLink())
                .build();
        Contract res= contractRepository.save(contract);
        return res;

    }

    @Override
    public Contract findContractById(Long id) {
        Contract contract = contractRepository.findById(id).orElseThrow(()->new AppBadRequestException("Contract not found"));
    return contract;
    }

    @Override
    public Contract findContractBySlug(String slug) {
        Contract contract = contractRepository.findBySlug(slug).orElseThrow(()->new AppBadRequestException("Contract not found"));
        return contract;
    }

}
