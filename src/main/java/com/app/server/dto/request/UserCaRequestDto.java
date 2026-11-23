package com.app.server.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserCaRequestDto {

    private String userSlug;
    private String companyCaSlug;
    private String privateKeyPassword;


    private BigDecimal amount;
}
