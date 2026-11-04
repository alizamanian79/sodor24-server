package com.app.server.util.signature_service_producer.dto.request.ca;

import lombok.Data;

@Data
public class CreatePersonCertificateRequestDto {


    private String companyId;

    private String userId;

    private String username;

    private String department;

    private String country;

    private String state;

    private String location;

    private String email;

    private int validityDays;



    private String privateKeyPassword;


    private String caPrivateKeyPassword;


    }