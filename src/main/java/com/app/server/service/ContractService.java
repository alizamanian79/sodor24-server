package com.app.server.service;

import com.app.server.dto.request.ContractRequestDto;
import com.app.server.model.Contract;

import java.util.List;

public interface ContractService {
    List<Contract> contractList ();
    Contract contractPreparation(ContractRequestDto req);
    Contract findContractById(Long contractId);
    Contract findContractBySlug(String contractSlug) ;
}
