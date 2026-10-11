package com.app.server.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.Random;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "companies_ca",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_companies_ca_slug",
                        columnNames = "slug"
                )
        }
)
public class CompaniesCa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;



    @JsonManagedReference
    @NotNull(message = "شرکت الزامی است")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "company_id",
            nullable = false
    )
    private Company company;


    @JsonManagedReference
    @NotNull(message = "پلن اشتراک الزامی است")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "subscription_plan_id",
            nullable = false
    )
    private CompanySubscriptionPlan subscriptionPlan;




    @NotNull(message = "مدت اعتبار الزامی است")
    @Min(value = 1, message = "مدت اعتبار باید حداقل 1 روز باشد")
    @Column(nullable = false)
    private Integer validityDays;

    @NotNull(message = "تعداد کاربران الزامی است")
    @Min(value = 1, message = "تعداد کاربران باید حداقل 1 نفر باشد")
    @Column(nullable = false)
    private Integer usersCount;

    @Column(nullable = false)
    private boolean isActive = true;

    @NotBlank(message = "وضعیت الزامی است")
    @Size(max = 50, message = "وضعیت نمی‌تواند بیشتر از 50 کاراکتر باشد")
    @Column(nullable = false, length = 50)
    private String status;

    @Size(min = 6, max = 6, message = "شناسه باید دقیقاً 6 رقم باشد")
    @Pattern(
            regexp = "\\d{6}",
            message = "شناسه باید شامل دقیقاً 6 رقم باشد"
    )
    @Column(nullable = false, unique = true, length = 6)
    private String slug;

    @PrePersist
    private void generateSlug() {
        if (slug == null || slug.isBlank()) {
            slug = generateSixDigitSlug();
        }
    }

    private String generateSixDigitSlug() {
        return String.valueOf(
                100000 + new Random().nextInt(900000)
        );
    }
}