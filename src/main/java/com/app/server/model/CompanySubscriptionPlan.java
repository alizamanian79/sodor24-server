package com.app.server.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
        name = "company_subscription_plans",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_company_subscription_plan_slug",
                        columnNames = "slug"
                )
        }
)
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompanySubscriptionPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false, length = 100)
    private String title;


    @Column(length = 500)
    private String description;


    @Column(columnDefinition = "TEXT", nullable = false)
    private String completedDescription;


    @ElementCollection
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


    @Column(nullable = false, unique = true, length = 6, updatable = false)
    private String slug;


    @PrePersist
    protected void onCreate() {

        LocalDateTime now = LocalDateTime.now();

        createdAt = now;
        updatedAt = now;

        if (slug == null || slug.isBlank()) {
            slug = generateSixDigitSlug();
        }
    }


    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }


    private String generateSixDigitSlug() {
        return String.valueOf(
                100000 + new java.util.Random().nextInt(900000)
        );
    }
}