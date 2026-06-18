package com.example.springboot_backend.account.infrastructure.persistence;

import com.example.springboot_backend.account.domain.model.AccountStatus;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "user_accounts", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
public class JpaUserAccountEntity {
    @Id
    private UUID id;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String passwordHash;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountStatus status;
    @Column(nullable = false)
    private Instant registeredAt;
    private Instant activatedAt;
    private String verificationToken;
    private Instant verificationTokenExpiresAt;

    protected JpaUserAccountEntity() {}

    public JpaUserAccountEntity(UUID id, String email, String passwordHash, AccountStatus status,
                                Instant registeredAt, Instant activatedAt, String verificationToken,
                                Instant verificationTokenExpiresAt) {
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.status = status;
        this.registeredAt = registeredAt;
        this.activatedAt = activatedAt;
        this.verificationToken = verificationToken;
        this.verificationTokenExpiresAt = verificationTokenExpiresAt;
    }

    public UUID getId() { return id; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public AccountStatus getStatus() { return status; }
    public Instant getRegisteredAt() { return registeredAt; }
    public Instant getActivatedAt() { return activatedAt; }
    public String getVerificationToken() { return verificationToken; }
    public Instant getVerificationTokenExpiresAt() { return verificationTokenExpiresAt; }
}
