package com.app.server.util.signature_service_producer.dto.request.ca;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VerifyPersonCertificateDto {

    private String companyId;
    private String userId;
}
