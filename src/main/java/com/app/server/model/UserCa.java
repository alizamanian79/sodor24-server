
package com.app.server.model;

import com.app.server.model.companies.CompanyCa;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDateTime;



@Entity
@Table(
        name = "users_ca"
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserCa {


    private static final int SLUG_LENGTH = 8;
    private static final SecureRandom RANDOM = new SecureRandom();


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "user_id",
            nullable = false
    )
    private User user;

    @JsonBackReference
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
            length = SLUG_LENGTH,
            updatable = false
    )
    private String slug;

    @PrePersist
    private void generateSlug() {
        if (this.slug == null || this.slug.isBlank()) {
            this.slug = randomSlug();
        }
    }

    public static String randomSlug() {
        StringBuilder sb = new StringBuilder(SLUG_LENGTH);
        // رقم اول صفر نباشه (اختیاری)
        sb.append(1 + RANDOM.nextInt(9));
        for (int i = 1; i < SLUG_LENGTH; i++) {
            sb.append(RANDOM.nextInt(10));
        }
        return sb.toString();
    }


    @Column(nullable = false)
    private boolean isValid;

    @Column(nullable = false)
    private boolean isActive;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;


    private String caId;

    private BigDecimal amount;

    private String status;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String privateKeyPassword;



    private int validityDays;


    private int signatureAmount;
    private int totalSignatureAmount;



}
