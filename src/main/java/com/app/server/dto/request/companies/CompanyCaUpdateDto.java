package com.app.server.dto.request.companies;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyCaUpdateDto {

    @NotNull(message = "شناسه اشتراک نمیتواند خالی باشد")
    private String slug;

    @NotNull(message = "مدت اعتبار الزامی است")
    @Min(value = 1, message = "مدت اعتبار باید حداقل 1 روز باشد")
    private Integer validityDays;

    @NotNull(message = "تعداد کاربران الزامی است")
    @Min(value = 1, message = "تعداد کاربران باید حداقل 1 نفر باشد")
    private Integer usersCount;

    @NotNull(message = "تعداد امضاها الزامی است")
    @Min(value = 1, message = "تعداد امضاها باید حداقل 1 باشد")
    private Integer signaturesCount;

}