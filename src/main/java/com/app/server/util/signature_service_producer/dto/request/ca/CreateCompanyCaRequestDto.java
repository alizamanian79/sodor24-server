package com.app.server.util.signature_service_producer.dto.request.ca;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CreateCompanyCaRequestDto {

    private String companyId;

    private String companyName;

    private String country;

    private String state;

    private String location;

    private int validityDays;

    private String privateKeyPassword;
}