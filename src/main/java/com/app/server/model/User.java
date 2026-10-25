package com.app.server.model;
import com.app.server.model.companies.Company;
import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.io.Serializable;
import java.util.*;

@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "username"
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "users")
public class User implements  Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    @NotBlank(message = "شماره تماس نمی‌تواند خالی باشد")
    @Pattern(regexp = "\\d{11}", message = "شماره تماس باید دقیقا 11 رقم باشد (0912xxxxxxx)")
    private String phoneNumber;


    private String username;

    @Column(unique = true,nullable = false)
    private String nationalCode;

    private String sub;
    private boolean isValid;
    private String walletId;


    @ManyToMany(mappedBy = "owners")
    private Set<Company> companies = new HashSet<>();


}
