package com.app.server.util.wallet_service_producer.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActivityRequestDto {

    @NotBlank(message = "ایدی کاربر نمیتواند خالی باشد")
    private String sub;

    private boolean value;
}
