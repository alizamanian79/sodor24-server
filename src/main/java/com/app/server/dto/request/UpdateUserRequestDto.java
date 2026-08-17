package com.app.server.dto.request;

import com.app.server.model.Role;
import com.app.server.model.Signature;
import com.app.server.model.UserContract;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateUserRequestDto {

    @NotBlank(message = "وارد کردن ایمیل الزامی است")
    private String username;

    @NotBlank(message = "وارد کردن ایمیل الزامی است")
    @Email(message = "فرمت ایمیل صحیح نیست")
    private String email;

    @NotBlank(message = "وارد کردن نام الزامی است")
    @Size(min = 2, max = 50, message = "نام باید بین 2 تا 50 کاراکتر باشد")
    private String firstName;


    @NotBlank(message = "وارد کردن نام خانوادگی الزامی است")
    @Size(min = 2, max = 50, message = "نام خانوادگی باید بین 2 تا 50 کاراکتر باشد")
    private String lastName;

    @Size(min = 1, max = 15, message = "کدملی باید بین 1 تا 15 کاراکتر باشد")
    @NotBlank(message = "کد ملی نمیتواند خالی باشد")
    private String nationalCode;


    private String password;

    @NotBlank(message = "شماره تماس نمی‌تواند خالی باشد")
    @Pattern(regexp = "\\d{11}", message = "شماره تماس باید دقیقا 11 رقم باشد (0912xxxxxxx)")
    private String phoneNumber;


}
