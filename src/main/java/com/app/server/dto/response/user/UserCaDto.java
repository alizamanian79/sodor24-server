package com.app.server.dto.response.user;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class UserCaDto {

    private Long id;
    private String slug;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String caId;
    private BigDecimal amount;
    private String status;
    private Boolean active;
    private Boolean valid;
}
