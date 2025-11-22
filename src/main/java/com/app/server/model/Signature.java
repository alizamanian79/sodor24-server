package com.app.server.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;


@Getter
@Setter
@ToString
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "signatures")
public class Signature {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;


    @ManyToOne
    @JsonBackReference
    private User user;


    @Column(nullable = false, unique = true)
    private String signatureId;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
    private LocalDateTime expiredAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private String slug;
    private Long price;
    private int usageCount;
    private boolean isValid;

    private int totalUsageCount;

    @PrePersist
    public void prePersist() {

        if (slug == null) slug = UUID.randomUUID().toString();
        if (signatureId == null) signatureId = UUID.randomUUID().toString();
        if (totalUsageCount == 0) totalUsageCount = usageCount;
    }

}
