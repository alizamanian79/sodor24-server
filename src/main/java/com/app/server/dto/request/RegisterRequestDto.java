package com.app.server.dto.request;

import com.app.server.annotation.UniquePhoneNumber;
import com.app.server.annotation.UniqueUserName;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.UniqueElements;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequestDto {

    @UniqueUserName(message = "این نام کاربری وجود دارد")
    @NotBlank(message = "فیلد نام کاربری نمیتواند خالی باشد")
    private String username;

    private String password;

    @NotBlank(message = "شماره تماس نمیتواند خالی باشد")
    @Pattern(regexp = "\\d{11}", message = "شماره تماس باید دقیقا 11 رقم باشد (0912xxxxxxx)")
    @UniquePhoneNumber(message = "این شماره تماس قبلاً استفاده شده است")
    private String phoneNumber;
}
