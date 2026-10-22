package com.app.server.dto.request.users;

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
public class VerifyAccountRequestDto {

    @NotBlank(message = "شماره تماس نمی‌تواند خالی باشد")
    @Pattern(
            regexp = "^09\\d{9}$",
            message = "شماره تماس باید معتبر و دقیقا 11 رقم باشد (0912xxxxxxx)"
    )
    private String phoneNumber;

    @NotBlank(message = "کد تایید نمی‌تواند خالی باشد")
    @Pattern(
            regexp = "^\\d{6}$",
            message = "کد تایید باید دقیقا 6 رقم باشد"
    )
    private String otp;
}