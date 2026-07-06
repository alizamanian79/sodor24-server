package com.app.server.util.wallet_service_producer.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentVerifierRequestDto<D> {
    @NotBlank(message = "شناسه کاربر (sub) نمی‌تواند خالی باشد")
    private String sub;
    private Integer slug;

    private String callbackUrl;
    private String gateway;

    private D data;
}
