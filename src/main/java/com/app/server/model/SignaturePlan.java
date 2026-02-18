package com.app.server.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@ToString(exclude = "users")
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "signature_plan")
public class SignaturePlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "عنوان نمی‌تواند خالی باشد")
    @Size(min = 3, max = 100, message = "عنوان باید بین ۳ تا ۱۰۰ کاراکتر باشد")
    private String title;

    @Size(max = 500, message = "توضیحات نمی‌تواند بیشتر از ۵۰۰ کاراکتر باشد")
    private String description;

    @NotNull(message = "قیمت الزامی است")
    @Positive(message = "قیمت باید بزرگ‌تر از صفر باشد")
    @Column(nullable = false)
    private Long price;

    @Min(value = 0, message = "تعداد استفاده نمی‌تواند منفی باشد")
    private int usageCount;

    @Min(value = 1, message = "مدت اعتبار باید حداقل ۱ روز باشد")
    private int period;


    private boolean isActive;

    @ElementCollection
    @CollectionTable(name = "signature_features", joinColumns = @JoinColumn(name = "signature_id"))
    @Column(name = "feature")
    private List<String> features;

    @ElementCollection
    @CollectionTable(name = "signature_tags", joinColumns = @JoinColumn(name = "signature_id"))
    @Column(name = "tag")
    private Set<String> tags;



    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JsonIgnore
    private List<UserSignature> users;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
