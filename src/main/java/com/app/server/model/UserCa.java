
package com.app.server.model;

import com.app.server.model.companies.CompanyCa;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;


@Entity
@Table(
        name = "users_ca",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_users_ca_slug",
                        columnNames = "user_ca_slug"
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


    @ManyToMany
    @JoinTable(
            name = "companies_ca_user_ca",
            joinColumns = @JoinColumn(name = "user_ca_id"),
            inverseJoinColumns = @JoinColumn(name = "company_ca_id")
    )
    @Builder.Default
    private Set<CompanyCa> companyCas = new HashSet<>();


    @ManyToMany
    @JoinTable(
            name = "users_user_ca",
            joinColumns = @JoinColumn(name = "user_ca_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @Builder.Default
    private Set<User> users = new HashSet<>();







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