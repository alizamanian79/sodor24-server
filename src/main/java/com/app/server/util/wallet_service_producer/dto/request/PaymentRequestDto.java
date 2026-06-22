package com.app.server.util.wallet_service_producer.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentRequestDto {

    @NotBlank(message = "شناسه کاربر (sub) نمی‌تواند خالی باشد")
    private String sub;

    @NotNull(message = "مبلغ الزامی است")
    @DecimalMin(value = "0.01", message = "مبلغ باید بیشتر از 0 باشد")
    private BigDecimal amount;

    @NotBlank(message = "وضعیت یا process نمی‌تواند خالی باشد(deposit/withdraw)")
    private String process;

    @Pattern(
            regexp = "^(\\+?[0-9]{10,15})?$",
            message = "شماره تلفن معتبر نیست"
    )
    private String phoneNumber;

    @Email(message = "ایمیل معتبر نیست")
    private String email;


    private String description;

    @NotBlank(message = "callbackUrl نمی‌تواند خالی باشد")
    private String callbackUrl;

    @NotBlank(message = "نام سرویس پرداخت نمی‌تواند خالی باشد")
    private String paymentServiceName;
}