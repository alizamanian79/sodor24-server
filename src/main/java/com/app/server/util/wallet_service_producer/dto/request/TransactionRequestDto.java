package com.app.server.util.wallet_service_producer.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionRequestDto {

    @NotBlank(message = "ایدی کاربر نمیتواند خالی باشد")
    private String sub;

    @NotNull(message = "مبلغ الزامی است")
    @DecimalMin(value = "1000", message = "حداقل مبلغ ۱۰۰۰ تومان است")
    @Digits(integer = 15, fraction = 0, message = "مبلغ معتبر نیست")
    private BigDecimal amount;

    @Size(max = 255, message = "توضیحات حداکثر ۲۵۵ کاراکتر")
    private String description;

    @Size(max = 255, message = "توضیحات حداکثر ۲۵۵ کاراکتر")
    private String process;

    private String status;

}
