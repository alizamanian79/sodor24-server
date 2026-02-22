package com.app.server.model;
import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "username"
)
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User implements UserDetails, Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;

    private String username;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @NotBlank(message = "شماره تماس نمی‌تواند خالی باشد")
    @Pattern(regexp = "\\d{11}", message = "شماره تماس باید دقیقا 11 رقم باشد (0912xxxxxxx)")
   private String phoneNumber;



    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Set<Role> roles = new HashSet<>();



    @OneToMany(fetch = FetchType.EAGER,
            mappedBy = "user",
            orphanRemoval = true)
    private List<Signature> signatures;



    @OneToOne(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    private Contract contract;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL,mappedBy = "user")
    private Set<UserContract> signedContract = new HashSet<>();




    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();
        for (Role role : roles) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.name()));
            authorities.addAll(role.getAuthorities().stream()
                    .map(authority -> new SimpleGrantedAuthority(authority.name()))
                    .collect(Collectors.toSet()));
        }
        return authorities;
    }









    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }


    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
