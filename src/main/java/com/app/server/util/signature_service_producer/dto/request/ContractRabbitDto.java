package com.app.server.util.signature_service_producer.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContractRabbitDto {

    private String fileName;
    private String file;

    private String keyFileName;
    private String privateKeyFile;

    private String keyPassword;
    private String reason;
    private String country;

}