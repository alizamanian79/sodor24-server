package com.app.server.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChargeSignatureDto {

    private Long signatureId;
    private Long userId;
    private Long signaturePlanId;

}
