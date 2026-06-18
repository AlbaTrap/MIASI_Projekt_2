package com.example.springboot_backend.account.infrastructure.persistence;

import com.example.springboot_backend.account.domain.model.SessionStatus;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "user_sessions", indexes = @Index(name = "idx_user_sessions_token", columnList = "token"))
public class JpaUserSessionEntity {
    @Id
    private UUID id;
    @Column(nullable = false)
    private UUID accountId;
    @Column(nullable = false, unique = true, length = 500)
    private String token;
    @Column(nullable = false)
    private Instant tokenExpiresAt;
    @Column(nullable = false)
    private Instant createdAt;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SessionStatus status;

    protected JpaUserSessionEntity() {}

    public JpaUserSessionEntity(UUID id, UUID accountId, String token, Instant tokenExpiresAt, Instant createdAt, SessionStatus status) {
        this.id = id;
        this.accountId = accountId;
        this.token = token;
        this.tokenExpiresAt = tokenExpiresAt;
        this.createdAt = createdAt;
        this.status = status;
    }

    public UUID getId() { return id; }
    public UUID getAccountId() { return accountId; }
    public String getToken() { return token; }
    public Instant getTokenExpiresAt() { return tokenExpiresAt; }
    public Instant getCreatedAt() { return createdAt; }
    public SessionStatus getStatus() { return status; }
    public void setStatus(SessionStatus status) { this.status = status; }
}
