package com.app.server.service;

import com.app.server.model.UserContract;

public interface UserContractService {
    public UserContract signedContract(Long userId , Long contractId);
}
