package com.app.server.service;

import com.app.server.dto.request.ContractRequestDto;
import com.app.server.model.Contract;

import java.io.IOException;
import java.util.List;

public interface ContractService {
    List<Contract> contractList ();
    Contract preparationContract(ContractRequestDto req) throws Exception;
    Contract findContractById(Long contractId);
    Contract findContractBySlug(String slug);
    String deleteContractById(Long id);
    boolean isExistContract(String slug);
}
