package com.app.server.dto.response.user;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class WalletTransactionDto {

    private Long id;
    private Long walletId;
    private BigDecimal amount;
    private String description;
    private String process;
    private String gateway;
    private BigDecimal balanceAfter;
    private LocalDateTime createdAt;
    private String status;
    private Long slug;
}
