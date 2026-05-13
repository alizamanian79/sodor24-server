package com.app.server.controller;

import com.app.server.dto.request.ContractRequestDto;
import com.app.server.model.Contract;
import com.app.server.model.User;
import com.app.server.service.ContractService;
import com.app.server.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/contract")
@RequiredArgsConstructor
public class ContractController {

    private final ContractService contractService;
    private final UserService userService;


    @GetMapping
    public ResponseEntity<?> allContract() {
        List<Contract> res = contractService.contractList();
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id){
        return new ResponseEntity<>(contractService.findContractById(id),HttpStatus.OK);
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<?> getBySlug(@PathVariable String slug){
        return new ResponseEntity<>(contractService.findContractBySlug(slug),HttpStatus.OK);
    }


    @PostMapping(value = "/preparation",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> contractPreparation(
            @ModelAttribute ContractRequestDto req, Authentication auth) throws Exception {

        User user = userService.convertUserFromAuthentication(auth);
        req.setUserId(user.getId());

        Contract contract = contractService.preparationContract(req);
        return new ResponseEntity<>(contract, HttpStatus.OK);
    }


//    @PostMapping(value = "/preparation",
//            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity<?> contractPreparation(
//            @RequestParam RMQContractRequestDto req) {
//        System.out.println(req);
//        return new ResponseEntity<>("ok", HttpStatus.OK);
//    }


}
