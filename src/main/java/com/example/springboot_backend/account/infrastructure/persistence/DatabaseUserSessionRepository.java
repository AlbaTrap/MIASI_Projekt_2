package com.example.springboot_backend.account.infrastructure.persistence;

import com.example.springboot_backend.account.domain.model.SessionStatus;
import com.example.springboot_backend.account.domain.model.UserSession;
import com.example.springboot_backend.account.domain.repository.UserSessionRepository;
import com.example.springboot_backend.account.domain.valueobject.*;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public class DatabaseUserSessionRepository implements UserSessionRepository {
    private final SpringDataJpaUserSessionRepository repository;

    public DatabaseUserSessionRepository(SpringDataJpaUserSessionRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserSession save(UserSession session) { return toDomain(repository.save(toJpa(session))); }

    @Override
    public Optional<UserSession> findByToken(AccessToken token) {
        return repository.findByToken(token.value()).map(this::toDomain);
    }

    @Override
    public List<UserSession> findByAccountId(UserAccountId accountId) {
        return repository.findByAccountId(accountId.value()).stream().map(this::toDomain).toList();
    }

    @Override
    public void invalidateSessionsOfAccount(UserAccountId accountId) {
        var sessions = repository.findByAccountId(accountId.value());
        sessions.forEach(s -> s.setStatus(SessionStatus.INVALIDATED));
        repository.saveAll(sessions);
    }

    private JpaUserSessionEntity toJpa(UserSession session) {
        return new JpaUserSessionEntity(session.id().value(), session.accountId().value(), session.token().value(),
                session.token().expiresAt(), session.createdAt(), session.status());
    }

    private UserSession toDomain(JpaUserSessionEntity entity) {
        return new UserSession(UserSessionId.of(entity.getId()), UserAccountId.of(entity.getAccountId()),
                new AccessToken(entity.getToken(), entity.getTokenExpiresAt()), entity.getCreatedAt(), entity.getStatus());
    }
}
