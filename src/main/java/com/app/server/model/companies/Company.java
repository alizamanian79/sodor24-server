package com.app.server.model.companies;

import com.app.server.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(
        name = "companies",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_company_slug", columnNames = "slug"),
                @UniqueConstraint(name = "uk_company_name", columnNames = "company_name")
        }
)
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Company {

    private static final SecureRandom RANDOM = new SecureRandom();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // uniqueness is declared once, in @Table
    @Column(nullable = false, length = 6, updatable = false)
    private String slug;

    @NotBlank(message = "اسم شرکت درخواست کننده گواهی نمیتواند خالی باشد")
    @Size(max = 150, message = "اسم شرکت نمیتواند بیشتر از ۱۵۰ کاراکتر باشد")
    @Column(name = "company_name", nullable = false, length = 150)
    private String companyName;

    private String description;

    private String completeDescriptions;

    @Size(max = 255, message = "موقعیت مکانی نمیتواند بیشتر از ۲۵۵ کاراکتر باشد")
    private String location;

    @Size(max = 100, message = "استان نمیتواند بیشتر از ۱۰۰ کاراکتر باشد")
    private String state;

    @Size(max = 100, message = "کشور نمیتواند بیشتر از ۱۰۰ کاراکتر باشد")
    private String country;

    // never returned in responses. Store it hashed/encrypted, not as plain text.
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotBlank(message = "رمز کلید خصوصی نمیتواند خالی باشد")
    @Size(min = 12, max = 255, message = "رمز کلید خصوصی باید بین 12 تا ۲۵۵ کاراکتر باشد")
    @Column(nullable = false)
    private String privateKeyPassword;

    // ---------- Relations ----------

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "company_owners",
            joinColumns = @JoinColumn(name = "company_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @JsonIgnore
    @Builder.Default
    private Set<User> owners = new HashSet<>();

    // Child side. Never serialize it from the entity (breaks the Company <-> CompanyCa loop).
    @OneToMany(
            mappedBy = "company",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JsonIgnore
    @Builder.Default
    private Set<CompanyCa> companyCa = new HashSet<>();

    // ---------- Timestamps / flags ----------

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private boolean isValid;
    private boolean isActive;

    // ---------- Helpers (keep both sides of the relation in sync) ----------

    public void addCompanyCa(CompanyCa ca) {
        companyCa.add(ca);
        ca.setCompany(this);
    }

    public void removeCompanyCa(CompanyCa ca) {
        companyCa.remove(ca);
        ca.setCompany(null);
    }

    public void addOwner(User user) {
        owners.add(user);
    }

    public void removeOwner(User user) {
        owners.remove(user);
    }

    // ---------- Lifecycle ----------

    @PrePersist
    private void generateSlug() {
        if (slug == null || slug.isBlank()) {
            slug = String.valueOf(100000 + RANDOM.nextInt(900000));
        }
    }

    // ---------- equals / hashCode (id based, proxy safe) ----------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Company other = (Company) o;
        return id != null && Objects.equals(id, other.getId());
    }

    @Override
    public int hashCode() {
        return Hibernate.getClass(this).hashCode();
    }
}