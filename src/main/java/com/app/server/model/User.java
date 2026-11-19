package com.app.server.model;
import com.app.server.model.companies.Company;
import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.io.Serializable;
import java.security.SecureRandom;
import java.util.*;

@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "sub"
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "users")
public class User implements Serializable{

    private static final int SLUG_LENGTH = 8;
    private static final SecureRandom RANDOM = new SecureRandom();


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sub;

    private String walletId;

    @ManyToMany(mappedBy = "owners")
    private Set<Company> companies = new HashSet<>();



    @JsonManagedReference
    @OneToMany(
            mappedBy = "user",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    private Set<UserCa> userCas = new HashSet<>();

    @Column(
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
        sb.append(1 + RANDOM.nextInt(9));
        for (int i = 1; i < SLUG_LENGTH; i++) {
            sb.append(RANDOM.nextInt(10));
        }
        return sb.toString();
    }


}
