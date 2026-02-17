package com.app.server.model;


import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;


@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "slug"
)

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "contracts")
public class Contract {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String title;
    private String description;
    private String pdf;
    private String signedLink;
    private String unSignedLink;

    @OneToOne
    private User owner;


    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL,mappedBy = "contract")
    private Set<UserContract> signers = new HashSet<>();


    @Column(unique = true)
    private String slug;

    @PrePersist
    protected void onCreate() {
        Random random = new Random();
        int randomNumber = 5 + random.nextInt(9000);
        this.slug = String.valueOf(randomNumber);
    }

}
