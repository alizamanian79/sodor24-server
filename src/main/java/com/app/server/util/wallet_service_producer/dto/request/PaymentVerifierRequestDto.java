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

    @NotNull(message = "مبلغ الزامی است")
    @DecimalMin(value = "0.01", message = "مبلغ باید بیشتر از 0 باشد")
    private BigDecimal amount;


    @NotBlank(message = "نام سرویس پرداخت نمی‌تواند خالی باشد")
    private String paymentServiceName;

    private String description;
    private String callBackUrl;

    private D data;
}
