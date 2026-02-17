package com.app.server.controller;

import com.app.server.dto.request.ContractRequestDto;
import com.app.server.dto.request.SigningDto;
import com.app.server.model.Contract;
import com.app.server.model.UserContract;
import com.app.server.service.ContractService;
import com.app.server.service.UserContractService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/contract")
@RequiredArgsConstructor
public class ContractController {

    private final ContractService contractService;

    private final UserContractService userContractService;


    @GetMapping
    public ResponseEntity<?> allContract() {
        List<Contract> res = contractService.contractList();
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PostMapping("/preparation")
    public ResponseEntity<?> contractPreparation(@RequestBody ContractRequestDto contract) {
        Contract res = contractService.contractPreparation(contract);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PostMapping("/signing")
    public ResponseEntity<?> contractPreparation(@RequestBody SigningDto req) {
        UserContract res = userContractService.signedContract(req.getUserId(),req.getContractId());
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

}
