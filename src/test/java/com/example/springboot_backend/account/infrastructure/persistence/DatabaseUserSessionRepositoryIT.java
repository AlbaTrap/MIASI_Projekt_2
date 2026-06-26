package com.example.springboot_backend.account.infrastructure.persistence;

import com.example.springboot_backend.account.domain.model.SessionStatus;
import com.example.springboot_backend.account.domain.model.UserSession;
import com.example.springboot_backend.account.domain.valueobject.AccessToken;
import com.example.springboot_backend.account.domain.valueobject.UserAccountId;
import com.example.springboot_backend.account.domain.valueobject.UserSessionId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@WebMvcTest(DatabaseUserSessionRepository.class)
class DatabaseUserSessionRepositoryIT {

    @MockitoBean
    private SpringDataJpaUserSessionRepository springDataRepository;

    private DatabaseUserSessionRepository repository;

    @BeforeEach
    void setUp() {
        repository = new DatabaseUserSessionRepository(springDataRepository);
    }

    @Test
    void save() {
        // Given
        UserSession session = createTestSession();
        JpaUserSessionEntity entity = toEntity(session);
        when(springDataRepository.save(any(JpaUserSessionEntity.class))).thenReturn(entity);

        // When
        UserSession savedSession = repository.save(session);

        // Then
        assertThat(savedSession).isNotNull();
        assertThat(savedSession.id().value()).isEqualTo(session.id().value());
        assertThat(savedSession.token().value()).isEqualTo(session.token().value());
    }

    @Test
    void findByToken() {
        // Given
        UserSession session = createTestSession();
        JpaUserSessionEntity entity = toEntity(session);
        when(springDataRepository.findByToken(session.token().value())).thenReturn(Optional.of(entity));

        // When
        Optional<UserSession> result = repository.findByToken(session.token());

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().token().value()).isEqualTo(session.token().value());
    }

    @Test
    void findByAccountId() {
        // Given
        UserAccountId accountId = UserAccountId.newId();
        UserSession session = createTestSession(accountId);
        JpaUserSessionEntity entity = toEntity(session);
        when(springDataRepository.findByAccountId(accountId.value())).thenReturn(List.of(entity));

        // When
        List<UserSession> results = repository.findByAccountId(accountId);

        // Then
        assertThat(results).hasSize(1);
        assertThat(results.get(0).accountId().value()).isEqualTo(accountId.value());
    }

    @Test
    void invalidateSessionsOfAccount() {
        // Given
        UserAccountId accountId = UserAccountId.newId();
        JpaUserSessionEntity entity = new JpaUserSessionEntity(
                UUID.randomUUID(), accountId.value(), "token", Instant.now().plusSeconds(3600), Instant.now(), SessionStatus.ACTIVE
        );
        when(springDataRepository.findByAccountId(accountId.value())).thenReturn(List.of(entity));

        // When
        repository.invalidateSessionsOfAccount(accountId);

        // Then
        assertThat(entity.getStatus()).isEqualTo(SessionStatus.INVALIDATED);
        verify(springDataRepository).saveAll(anyList());
    }

    private UserSession createTestSession() {
        return createTestSession(UserAccountId.newId());
    }

    private UserSession createTestSession(UserAccountId accountId) {
        return new UserSession(
                UserSessionId.newId(),
                accountId,
                new AccessToken("test-token", Instant.now().plusSeconds(3600)),
                Instant.now(),
                SessionStatus.ACTIVE
        );
    }

    private JpaUserSessionEntity toEntity(UserSession session) {
        return new JpaUserSessionEntity(
                session.id().value(),
                session.accountId().value(),
                session.token().value(),
                session.token().expiresAt(),
                session.createdAt(),
                session.status()
        );
    }
}