package com.app.server.service.impliment;

import com.app.server.model.Contract;
import com.app.server.model.User;
import com.app.server.model.UserContract;
import com.app.server.repository.UserContractRepository;
import com.app.server.service.ContractService;
import com.app.server.service.UserContractService;
import com.app.server.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserContractServiceImpl  implements UserContractService {

    private final UserContractRepository userContractRepository;
    private final UserService userService;
    private final ContractService contractService;

    public UserContract signedContract(Long userId , Long contractId){
        User user = userService.findUserById(userId);
        Contract contract = contractService.findContractById(contractId);

        System.out.println(user);
        System.out.println(contract);

        UserContract userContract = UserContract.builder()
                .user(user)
                .contract(contract)
                .build();
       return userContractRepository.save(userContract);
    }
}
