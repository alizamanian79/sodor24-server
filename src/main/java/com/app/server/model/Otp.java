package com.app.server.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id"
)
@Data
@AllArgsConstructor
@Entity
@Table(name = "otps")
public class Otp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 5)
    private String code;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    private boolean used = false;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // ─── Constructors ────────────────────────────────────────────────────────────

    public Otp() {}

    public Otp(String code, User user) {
        this.code      = code;
        this.user      = user;
        this.expiresAt = LocalDateTime.now().plusMinutes(1);
    }

    // ─── Business Logic ──────────────────────────────────────────────────────────

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiresAt);
    }

    public boolean isValid() {
        return !used && !isExpired();
    }

    // ─── Getters & Setters ───────────────────────────────────────────────────────

    public Long getId()                        { return id; }

    public String getCode()                    { return code; }
    public void   setCode(String code)         { this.code = code; }

    public LocalDateTime getExpiresAt()                      { return expiresAt; }
    public void          setExpiresAt(LocalDateTime exp)     { this.expiresAt = exp; }

    public boolean isUsed()                    { return used; }
    public void    setUsed(boolean used)       { this.used = used; }

    public User getUser()                      { return user; }
    public void setUser(User user)             { this.user = user; }

    public LocalDateTime getCreatedAt()        { return createdAt; }
    public LocalDateTime getUpdatedAt()        { return updatedAt; }
}