package com.app.server.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "companies")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String slug;
    private String companyName;
    private int validityDays;
    private String location;
    private String state;
    private String country;


    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String privateKeyPassword;



    @ManyToMany
    @JoinTable(
            name = "company_owners",
            joinColumns = @JoinColumn(name = "company_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> owners = new HashSet<>();


}
