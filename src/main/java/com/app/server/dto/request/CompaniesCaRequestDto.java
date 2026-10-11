package com.app.server.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompaniesCaRequestDto {

        @NotBlank(message = "شناسه شرکت الزامی است")
        private String companySlug;

        @NotBlank(message = "شناسه پلن اشتراک الزامی است")
        private String subscriptionPlanSlug;

}