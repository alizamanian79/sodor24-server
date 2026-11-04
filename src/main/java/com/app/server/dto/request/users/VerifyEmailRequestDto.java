package com.app.server.dto.request.users;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VerifyEmailRequestDto {

    @NotBlank(message = "ایمیل نمیتواند خالی باشد")
    @Email(message = "فرمت ایمیل صحیح نمیباشد")
    private String email;

    @NotBlank(message = "کد تایید نمیتواند خالی باشد")
    @Pattern(
            regexp = "^\\d{6}$",
            message = "کد تایید باید ۶ رقم باشد"
    )
    private String otp;

}
