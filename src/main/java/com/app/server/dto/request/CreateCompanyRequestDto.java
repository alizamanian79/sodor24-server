package com.app.server.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateCompanyRequestDto {


    @NotBlank(message = "اسم شرکت درخواست کننده گواهی نمیتواند خالی باشد")
    @Size(max = 150, message = "اسم شرکت نمیتواند بیشتر از ۱۵۰ کاراکتر باشد")
    @Column(nullable = false, length = 150)
    private String companyName;


    @Max(value = 365, message = "مدت اعتبار نمیتواند بیشتر از 365 روز باشد")
    @Column(nullable = false)
    private int validityDays;

    @Size(max = 255, message = "موقعیت مکانی نمیتواند بیشتر از ۲۵۵ کاراکتر باشد")
    private String location;

    @Size(max = 100, message = "استان نمیتواند بیشتر از ۱۰۰ کاراکتر باشد")
    private String state;

    @Size(max = 100, message = "کشور نمیتواند بیشتر از ۱۰۰ کاراکتر باشد")
    private String country;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotBlank(message = "رمز کلید خصوصی نمیتواند خالی باشد")
    @Size(min = 12, max = 255, message = "رمز کلید خصوصی باید بین 12 تا ۲۵۵ کاراکتر باشد")
    private String privateKeyPassword;


    private String userSub;

}
