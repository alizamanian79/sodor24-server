package com.app.server.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserCaRequestDto {

    @NotBlank(message = "شناسه کاربر الزامی است")
    private String userSlug;

    @NotBlank(message = "شناسه گواهی شرکت الزامی است")
    private String companyCaSlug;

    @ToString.Exclude // جلوگیری از لو رفتن رمز در لاگ
    @NotBlank(message = "رمز کلید خصوصی الزامی است")
    @Size(min = 8, max = 64, message = "رمز کلید خصوصی باید بین ۸ تا ۶۴ کاراکتر باشد")
    private String privateKeyPassword;

    @DecimalMin(value = "0.0", message = "مبلغ نمی‌تواند منفی باشد")
    private BigDecimal amount;

    @Min(value = 1, message = "تعداد امضا باید حداقل ۱ باشد")
    private int signatureCount;

    @Min(value = 1, message = "مدت اعتبار باید حداقل ۱ روز باشد")
    @Max(value = 3650, message = "مدت اعتبار نمی‌تواند بیش از ۳۶۵۰ روز باشد")
    private int validityDays;
}