package com.app.server.model.companies;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Objects;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "companies_ca",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_companies_ca_slug", columnNames = "slug")
        }
)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class CompanyCa {

    private static final SecureRandom RANDOM = new SecureRandom();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @NotNull(message = "شرکت الزامی است")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "company_id", nullable = false)
    @JsonIgnore
    private Company company;

    @NotNull(message = "پلن اشتراک الزامی است")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "subscription_plan_id", nullable = false)
    @JsonIgnore
    private CompanySubscriptionPlan subscriptionPlan;


    @JsonProperty("companyId")
    public Long getCompanyId() {
        return company != null ? company.getId() : null;
    }

    @JsonProperty("subscriptionPlanId")
    public Long getSubscriptionPlanId() {
        return subscriptionPlan != null ? subscriptionPlan.getId() : null;
    }



    @NotNull(message = "مدت اعتبار الزامی است")
    @Min(value = 1, message = "مدت اعتبار باید حداقل 1 روز باشد")
    @Column(nullable = false)
    private Integer validityDays;

    @NotNull(message = "تعداد کاربران الزامی است")
    @Min(value = 1, message = "تعداد کاربران باید حداقل 1 نفر باشد")
    @Column(nullable = false)
    private Integer usersCount;

    @NotNull(message = "تعداد امضاها الزامی است")
    @Min(value = 1, message = "تعداد امضاها باید حداقل 1 باشد")
    @Column(name = "signatures_count", nullable = false)
    private Integer signaturesCount;

    @Builder.Default
    @Column(nullable = false)
    private boolean isActive = true;

    @NotBlank(message = "وضعیت الزامی است")
    @Size(max = 50, message = "وضعیت نمی‌تواند بیشتر از 50 کاراکتر باشد")
    @Column(nullable = false, length = 50)
    private String status;

    @Size(min = 6, max = 6, message = "شناسه باید دقیقاً 6 رقم باشد")
    @Pattern(regexp = "\\d{6}", message = "شناسه باید شامل دقیقاً 6 رقم باشد")
    @Column(nullable = false, length = 6, updatable = false)
    private String slug;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;


    @Transient
    @JsonProperty("expiredAt")
    public LocalDateTime getExpiredAt() {
        if (createdAt == null || validityDays == null) {
            return null;
        }
        return createdAt.plusDays(validityDays);
    }


    @PrePersist
    private void generateSlug() {
        if (slug == null || slug.isBlank()) {
            slug = String.valueOf(100000 + RANDOM.nextInt(900000));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        CompanyCa other = (CompanyCa) o;
        return id != null && Objects.equals(id, other.getId());
    }

    @Override
    public int hashCode() {
        return Hibernate.getClass(this).hashCode();
    }
}