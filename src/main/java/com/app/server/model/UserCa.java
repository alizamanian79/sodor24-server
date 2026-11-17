
package com.app.server.model;

import com.app.server.model.companies.CompanyCa;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;



@Entity
@Table(
        name = "users_ca",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_users_ca_slug",
                        columnNames = "user_ca_slug"
                ),
                @UniqueConstraint(
                        name = "uk_user_company_ca",
                        columnNames = {"user_id", "company_ca_id"}
                )
        }
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserCa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // هر UserCa متعلق به یک کاربر است
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "user_id",
            nullable = false
    )
    private User user;

    // هر UserCa متعلق به یک CompanyCa است
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "company_ca_id",
            nullable = false
    )
    private CompanyCa companyCa;

    @Column(
            name = "user_ca_slug",
            nullable = false,
            unique = true,
            length = 6,
            updatable = false
    )
    private String userCaSlug;

    @Column(nullable = false)
    private boolean isValid;

    @Column(nullable = false)
    private boolean isActive;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(unique = true, nullable = false)
    private String caId;

    private BigDecimal amount;

    private String status;

    private String privateKeyPassword;
}
