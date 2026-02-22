package com.app.server.model;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;
import java.util.Random;




@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user_signature")
public class Signature {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;




    @ManyToOne
    @JoinColumn(name = "signature_plan_id")
    @JsonIdentityReference(alwaysAsId = true)
    private SignaturePlan signaturePlan;




    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIdentityReference(alwaysAsId = true)
    private User user;





    private boolean valid;
    private int usageCount;
    private int totalUsageCount;
    private String privateKeyId;
    private String privateKeyIdLink;
    private String otp;
    private String country;
    private String reason;
    private String location;
    private String organization;
    private String department;
    private String state;
    private String city;
    private String email;
    private String title;


    private LocalDateTime signatureExpired;
    private String signaturePassword;




    @CreationTimestamp
    private LocalDateTime createdAt;

    @CreationTimestamp
    private LocalDateTime updatedAt;


    @PrePersist
    public void prePersist() {
        this.totalUsageCount=usageCount;
        this.otp = String.valueOf(1000 + new Random().nextInt(9000));

    }
}
