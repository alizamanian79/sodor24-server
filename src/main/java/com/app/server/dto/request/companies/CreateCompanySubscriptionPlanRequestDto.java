package com.app.server.dto.request.companies;


import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.JoinColumn;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateCompanySubscriptionPlanRequestDto {


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

    @Column(name = "signatures_count", nullable = false)
    private Integer signaturesCount;

    @Column(nullable = false)
    private BigDecimal price;


}
