package com.app.server.model.companies;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(
        name = "company_subscription_plans",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_company_subscription_plan_slug", columnNames = "slug")
        }
)
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class CompanySubscriptionPlan {

    private static final SecureRandom RANDOM = new SecureRandom();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(length = 500)
    private String description;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String completedDescription;

    // EAGER: a small set of strings, and it avoids LazyInitializationException when serializing
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "company_subscription_plan_tags",
            joinColumns = @JoinColumn(name = "plan_id")
    )
    @Column(name = "tag", nullable = false, length = 100)
    @Builder.Default
    private Set<String> tags = new HashSet<>();

    @Column(nullable = false)
    private Integer validDays;

    @Column(nullable = false)
    private Integer usersCount;

    @Column(name = "signatures_count", nullable = false)
    private Integer signaturesCount;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal price;

    @Builder.Default
    @Column(nullable = false)
    private Boolean active = true;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    @Column(length = 100, updatable = false)
    private String createdBy;

    @Column(nullable = false, length = 6, updatable = false)
    private String slug;

    // ---------- Relation (inverse side). No cascade: deleting a plan must not delete subscriptions ----------

    @OneToMany(mappedBy = "subscriptionPlan", fetch = FetchType.LAZY)
    @JsonIgnore
    @Builder.Default
    private Set<CompanyCa> companyCa = new HashSet<>();

    public void addCompanyCa(CompanyCa ca) {
        companyCa.add(ca);
        ca.setSubscriptionPlan(this);
    }

    public void removeCompanyCa(CompanyCa ca) {
        companyCa.remove(ca);
        ca.setSubscriptionPlan(null);
    }

    // ---------- Lifecycle ----------

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;

        if (slug == null || slug.isBlank()) {
            slug = String.valueOf(100000 + RANDOM.nextInt(900000));
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // ---------- equals / hashCode (id based, proxy safe) ----------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        CompanySubscriptionPlan other = (CompanySubscriptionPlan) o;
        return id != null && Objects.equals(id, other.getId());
    }

    @Override
    public int hashCode() {
        return Hibernate.getClass(this).hashCode();
    }
}