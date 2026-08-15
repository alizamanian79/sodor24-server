package com.app.server.dto.request;

import com.app.server.annotation.UniquePhoneNumber;
import com.app.server.annotation.UniqueUserName;
import jakarta.validation.constraints.Email;
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
    @NotBlank(message = "نام کاربری نمی‌تواند خالی باشد")
    private String username;

    @NotBlank(message = "ایمیل نمی‌تواند خالی باشد")
    @Email(message = "فرمت ایمیل معتبر نیست")
    private String email;

    @NotBlank(message = "رمز عبور نمی‌تواند خالی باشد")
    private String password;

    @NotBlank(message = "نام نمی‌تواند خالی باشد")
    private String firstName;

    @NotBlank(message = "نام خانوادگی نمی‌تواند خالی باشد")
    private String lastName;

    @NotBlank(message = "شماره تماس نمی‌تواند خالی باشد")
    @Pattern(
            regexp = "^\\d{11}$",
            message = "شماره تماس باید دقیقاً 11 رقم باشد"
    )
    private String phoneNumber;

    @NotBlank(message = "کد ملی نمی‌تواند خالی باشد")
    private String nationalCode;




}
