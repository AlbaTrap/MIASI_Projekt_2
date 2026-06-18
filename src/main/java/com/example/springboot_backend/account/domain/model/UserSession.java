package com.example.springboot_backend.account.domain.model;

import com.example.springboot_backend.account.domain.valueobject.*;
import java.time.Instant;

public class UserSession {
    private final UserSessionId id;
    private final UserAccountId accountId;
    private final AccessToken token;
    private final Instant createdAt;
    private SessionStatus status;

    public UserSession(UserSessionId id, UserAccountId accountId, AccessToken token, Instant createdAt, SessionStatus status) {
        this.id = id;
        this.accountId = accountId;
        this.token = token;
        this.createdAt = createdAt;
        this.status = status;
    }

    public static UserSession create(UserAccountId accountId, AccessToken token) {
        return new UserSession(UserSessionId.newId(), accountId, token, Instant.now(), SessionStatus.ACTIVE);
    }

    public void invalidate() { status = SessionStatus.INVALIDATED; }

    public boolean expired() { return token.expired(); }

    public boolean valid() { return status == SessionStatus.ACTIVE && !expired(); }

    public UserSessionId id() { return id; }
    public UserAccountId accountId() { return accountId; }
    public AccessToken token() { return token; }
    public Instant createdAt() { return createdAt; }
    public SessionStatus status() { return expired() ? SessionStatus.EXPIRED : status; }
}
