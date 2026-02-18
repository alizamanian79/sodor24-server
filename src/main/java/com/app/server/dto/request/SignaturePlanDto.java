package com.app.server.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SignaturePlanDto {

    private Long id;

    @NotBlank(message = "عنوان نمی‌تواند خالی باشد")
    @Size(min = 3, max = 100, message = "عنوان باید بین ۳ تا ۱۰۰ کاراکتر باشد")
    private String title;

    @Size(max = 500, message = "توضیحات نمی‌تواند بیشتر از ۵۰۰ کاراکتر باشد")
    private String description;

    @NotNull(message = "قیمت الزامی است")
    @Positive(message = "قیمت باید بزرگ‌تر از صفر باشد")
    private Long price;

    @Min(value = 0, message = "تعداد استفاده نمی‌تواند منفی باشد")
    private int usageCount;

    @Min(value = 1, message = "مدت اعتبار باید حداقل ۱ روز باشد")
    private int period;

    private boolean isActive;

    private List<String> features;

    private Set<String> tags;

}
