package com.app.server.util.wallet_service_producer.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSubRequestDto {

    @NotBlank(message = "ایدی نمیتواند خالی باشد")
    private String sub;
    @NotBlank(message = "ایدی جدید نمیتواند خالی باشد")
    private String changedSub;
}
