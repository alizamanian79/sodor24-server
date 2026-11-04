package com.app.server.dto.request.users;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChangeEmailOtpRequestDto {

    @NotBlank(message = "ایمیل نمیتواند خالی باشد")
    @Email(message = "فرمت ایمیل صحیح نمیباشد")
    private String email;
}
