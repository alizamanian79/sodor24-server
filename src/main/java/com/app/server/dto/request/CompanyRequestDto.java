package com.app.server.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
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
public class CompanyRequestDto {


    @NotBlank(message = "اسم شرکت درخواست کننده گواهی نمیتواند خالی باشد")
    @Size(
            max = 150,
            message = "اسم شرکت نمیتواند بیشتر از ۱۵۰ کاراکتر باشد"
    )
    private String companyName;

    @Size(
            max = 500,
            message = "توضیحات نمیتواند بیشتر از ۵۰۰ کاراکتر باشد"
    )


    @Size(
            max = 255,
            message = "موقعیت مکانی نمیتواند بیشتر از ۲۵۵ کاراکتر باشد"
    )
    private String location;

    @Size(
            max = 100,
            message = "استان نمیتواند بیشتر از ۱۰۰ کاراکتر باشد"
    )
    private String state;

    @Size(
            max = 100,
            message = "کشور نمیتواند بیشتر از ۱۰۰ کاراکتر باشد"
    )
    private String country;

    @NotBlank(message = "رمز کلید خصوصی نمیتواند خالی باشد")
    @Size(
            min = 12,
            max = 255,
            message = "رمز کلید خصوصی باید بین 12 تا ۲۵۵ کاراکتر باشد"
    )
    private String privateKeyPassword;


    @NotBlank(message = "توضیحات شرکت درخواست کننده گواهی نمیتواند خالی باشد")
    private String description;

    @NotBlank(message = "توضیحات تکمیلی شرکت درخواست کننده گواهی نمیتواند خالی باشد")
    private String completeDescriptions;


    private String userSub;

}
