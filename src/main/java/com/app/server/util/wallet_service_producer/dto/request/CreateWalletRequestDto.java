package com.app.server.util.wallet_service_producer.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateWalletRequestDto {
    private String sub;
    private BigDecimal balance;
    private String currency;

}
