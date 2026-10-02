package com.app.server.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
        name = "companies",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_company_slug", columnNames = "slug")
        }
)
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 6)
    private String slug;

    @NotBlank(message = "اسم شرکت درخواست کننده گواهی نمیتواند خالی باشد")
    @Size(max = 150, message = "اسم شرکت نمیتواند بیشتر از ۱۵۰ کاراکتر باشد")
    @Column(nullable = false, length = 150)
    private String companyName;


    @Max(value = 365, message = "مدت اعتبار نمیتواند بیشتر از 365 روز باشد")
    @Column(nullable = false)
    private int validityDays;

    @Size(max = 255, message = "موقعیت مکانی نمیتواند بیشتر از ۲۵۵ کاراکتر باشد")
    private String location;

    @Size(max = 100, message = "استان نمیتواند بیشتر از ۱۰۰ کاراکتر باشد")
    private String state;

    @Size(max = 100, message = "کشور نمیتواند بیشتر از ۱۰۰ کاراکتر باشد")
    private String country;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotBlank(message = "رمز کلید خصوصی نمیتواند خالی باشد")
    @Size(min = 12, max = 255, message = "رمز کلید خصوصی باید بین 12 تا ۲۵۵ کاراکتر باشد")
    private String privateKeyPassword;



    @ManyToMany
    @JoinTable(
            name = "company_owners",
            joinColumns = @JoinColumn(name = "company_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @Builder.Default
    private Set<User> owners = new HashSet<>();

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @PrePersist
    private void generateSlug() {
        if (slug == null || slug.isBlank()) {
            slug = generateSixDigitSlug();
        }
    }

    private String generateSixDigitSlug() {
        return String.valueOf(
                100000 + new java.util.Random().nextInt(900000)
        );
    }
}