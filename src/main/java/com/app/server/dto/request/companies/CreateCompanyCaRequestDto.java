package com.app.server.dto.request.companies;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCompanyCaRequestDto {

        @NotBlank(message = "شناسه شرکت الزامی است")
        private String companySlug;

        @NotBlank(message = "شناسه پلن اشتراک الزامی است")
        private String subscriptionPlanSlug;

        @NotBlank(message = "رمز عبور نمی‌تواند خالی باشد")
        @Size(min = 8, message = "رمز عبور باید حداقل ۸ کاراکتر باشد")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).+$",
                message = "رمز عبور باید شامل حروف کوچک و بزرگ انگلیسی، حداقل یک عدد و یک کاراکتر خاص باشد"
        )
        private String privateKeyPassword;

}