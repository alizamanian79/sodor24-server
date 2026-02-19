package com.app.server.dto.request;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.JoinColumn;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SignaturePlanRequestDto {

    @NotBlank(message = "عنوان نمی‌تواند خالی باشد")
    @Size(min = 3, max = 100, message = "عنوان باید بین ۳ تا ۱۰۰ کاراکتر باشد")
    private String title;

    @NotNull(message = "توضیحات الزامی است")
    @NotBlank(message = "توضیحات نمی‌تواند خالی باشد")
    private String description;

    @NotNull(message = "قیمت الزامی است")
    @Positive(message = "قیمت باید بیشتر از صفر باشد")
    @Column(nullable = false)
    private Long price;

    @Min(value = 1, message = "تعداد استفاده پلن حدقل باید 1 باشد")
    @NotNull(message = "خالی نمیتواند باشد")
    private int usageCount;

    @Min(value = 1, message = "مدت اعتبار باید حداقل ۱ روز باشد")
    private int period;



    private Long creatorId;
    private Long updatedUserId;

    private boolean isActive;

    @ElementCollection
    @CollectionTable(name = "signature_features", joinColumns = @JoinColumn(name = "signature_id"))
    @Column(name = "feature")
    private List<String> features;

    @ElementCollection
    @CollectionTable(name = "signature_tags", joinColumns = @JoinColumn(name = "signature_id"))
    @Column(name = "tag")
    private Set<String> tags;



}
