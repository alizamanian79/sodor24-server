package com.app.server.dto.response.user;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class WalletDataDto {

    private String sub;
    private BigDecimal balance;
    private Boolean active;
    private String currency;
    private List<WalletTransactionDto> transactions;
}
