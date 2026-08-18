package com.app.server.model;
import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
public class User implements  Serializable {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;



    @NotBlank(message = "وارد کردن ایمیل الزامی است")
    private String username;


    @Column(unique = true)
    @NotBlank(message = "وارد کردن ایمیل الزامی است")
    @Email(message = "فرمت ایمیل صحیح نیست")
    private String email;

    @NotBlank(message = "وارد کردن نام الزامی است")
    @Size(min = 2, max = 50, message = "نام باید بین 2 تا 50 کاراکتر باشد")
    private String firstName;


    @NotBlank(message = "وارد کردن نام خانوادگی الزامی است")
    @Size(min = 2, max = 50, message = "نام خانوادگی باید بین 2 تا 50 کاراکتر باشد")
    private String lastName;


    @Column(unique = true)
    @Size(min = 1, max = 15, message = "کدملی باید بین 1 تا 15 کاراکتر باشد")
    @NotBlank(message = "کد ملی نمیتواند خالی باشد")
    private String nationalCode;




    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Column(unique = true)
    @NotBlank(message = "شماره تماس نمی‌تواند خالی باشد")
    @Pattern(regexp = "\\d{11}", message = "شماره تماس باید دقیقا 11 رقم باشد (0912xxxxxxx)")
    private String phoneNumber;



    private String sub;


    private String otp;

    private boolean isValid;

//    @ElementCollection(fetch = FetchType.EAGER)
//    @CollectionTable(
//            name = "user_roles",
//            joinColumns = @JoinColumn(name = "user_id")
//    )
//    @Enumerated(EnumType.STRING)
//    @Column(name = "role")
//    private Set<Role> roles = new HashSet<>();

    @OneToMany(fetch = FetchType.EAGER,
            cascade = CascadeType.ALL,
            mappedBy = "user",
            orphanRemoval = true)
    private List<Signature> signatures;


    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<UserContract> signedContract = new ArrayList<>();


    private String walletId;


}
