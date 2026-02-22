package com.app.server.dto.request;

import lombok.Data;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class SignatureRequestDto {


    private Long userId;

    @NotNull(message = "شناسه پلن امضا الزامی است")
    @Positive(message = "شناسه پلن امضا باید یک عدد مثبت باشد")
    private Long signaturePlanId;

    @NotBlank(message = "کشور الزامی است")
    @Size(max = 100, message = "کشور نمی‌تواند بیشتر از ۱۰۰ کاراکتر باشد")
    private String country;

    @NotBlank(message = "دلیل درخواست الزامی است")
    @Size(max = 500, message = "دلیل درخواست نمی‌تواند بیشتر از ۵۰۰ کاراکتر باشد")
    private String reason;

    @NotBlank(message = "موقعیت الزامی است")
    @Size(max = 200, message = "موقعیت نمی‌تواند بیشتر از ۲۰۰ کاراکتر باشد")
    private String location;

    @NotBlank(message = "سازمان الزامی است")
    @Size(max = 200, message = "نام سازمان نمی‌تواند بیشتر از ۲۰۰ کاراکتر باشد")
    private String organization;

    @NotBlank(message = "دپارتمان الزامی است")
    @Size(max = 200, message = "نام دپارتمان نمی‌تواند بیشتر از ۲۰۰ کاراکتر باشد")
    private String department;

    @NotBlank(message = "استان الزامی است")
    @Size(max = 100, message = "استان نمی‌تواند بیشتر از ۱۰۰ کاراکتر باشد")
    private String state;

    @NotBlank(message = "شهر الزامی است")
    @Size(max = 100, message = "شهر نمی‌تواند بیشتر از ۱۰۰ کاراکتر باشد")
    private String city;

    @NotBlank(message = "ایمیل الزامی است")
    @Email(message = "فرمت ایمیل صحیح نمی‌باشد")
    @Size(max = 150, message = "ایمیل نمی‌تواند بیشتر از ۱۵۰ کاراکتر باشد")
    private String email;

    @NotBlank(message = "عنوان الزامی است")
    @Size(max = 150, message = "عنوان نمی‌تواند بیشتر از ۱۵۰ کاراکتر باشد")
    private String title;

    @NotBlank(message = "رمز امضا الزامی است")
    @Size(min = 8, max = 100, message = "رمز امضا باید بین ۸ تا ۱۰۰ کاراکتر باشد")
    private String signaturePassword;

    private boolean valid;
}
