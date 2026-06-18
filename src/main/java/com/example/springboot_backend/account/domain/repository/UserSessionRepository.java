package com.example.springboot_backend.account.domain.repository;

import com.example.springboot_backend.account.domain.model.UserSession;
import com.example.springboot_backend.account.domain.valueobject.AccessToken;
import com.example.springboot_backend.account.domain.valueobject.UserAccountId;
import java.util.List;
import java.util.Optional;

public interface UserSessionRepository {
    UserSession save(UserSession session);
    Optional<UserSession> findByToken(AccessToken token);
    List<UserSession> findByAccountId(UserAccountId accountId);
    void invalidateSessionsOfAccount(UserAccountId accountId);
}
