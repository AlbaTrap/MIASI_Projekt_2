package com.example.springboot_backend.account.application.service;

import com.example.springboot_backend.account.application.command.LoginCommand;
import com.example.springboot_backend.account.application.command.LogoutCommand;
import com.example.springboot_backend.account.application.command.RefreshSessionCommand;
import com.example.springboot_backend.account.application.dto.LoginResponse;
import com.example.springboot_backend.account.domain.event.UserLoggedInEvent;
import com.example.springboot_backend.account.domain.event.UserLoggedOutEvent;
import com.example.springboot_backend.account.domain.model.AccountStatus;
import com.example.springboot_backend.account.domain.model.SessionStatus;
import com.example.springboot_backend.account.domain.model.UserAccount;
import com.example.springboot_backend.account.domain.model.UserSession;
import com.example.springboot_backend.account.domain.port.TokenGenerator;
import com.example.springboot_backend.account.domain.repository.UserAccountRepository;
import com.example.springboot_backend.account.domain.repository.UserSessionRepository;
import com.example.springboot_backend.account.domain.service.Authenticator;
import com.example.springboot_backend.account.domain.valueobject.*;
import com.example.springboot_backend.shared.event.DomainEventPublisher;
import com.example.springboot_backend.shared.exception.NotFoundException;
import com.example.springboot_backend.shared.exception.UnauthorizedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthenticationApplicationServiceTest {

    private UserAccountRepository accountRepository;
    private UserSessionRepository sessionRepository;
    private TokenGenerator tokenGenerator;
    private Authenticator authenticator;
    private DomainEventPublisher eventPublisher;
    private AuthenticationApplicationService service;

    @BeforeEach
    void setUp() {
        accountRepository = mock(UserAccountRepository.class);
        sessionRepository = mock(UserSessionRepository.class);
        tokenGenerator = mock(TokenGenerator.class);
        authenticator = mock(Authenticator.class);
        eventPublisher = mock(DomainEventPublisher.class);
        service = new AuthenticationApplicationService(accountRepository, sessionRepository, tokenGenerator, authenticator, eventPublisher);
    }

    @Test
    void login_shouldSucceedWhenCredentialsAreValid() {
        // given
        String email = "test@example.com";
        String password = "password";
        UserAccount account = createAccount(email);
        LoginCommand command = new LoginCommand(email, password);
        AccessToken token = new AccessToken("access-token", Instant.now().plus(1, ChronoUnit.HOURS));

        when(accountRepository.findByEmail(any(EmailAddress.class))).thenReturn(Optional.of(account));
        when(tokenGenerator.createAccessToken(account.id())).thenReturn(token);
        when(sessionRepository.save(any(UserSession.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        LoginResponse response = service.login(command);

        // then
        assertNotNull(response);
        assertEquals(account.id().value(), response.accountId());
        assertEquals(token.value(), response.accessToken());
        verify(authenticator).authenticate(account, password);
        verify(sessionRepository).save(any(UserSession.class));
        verify(eventPublisher).publish(any(UserLoggedInEvent.class));
    }

    @Test
    void login_shouldThrowUnauthorizedExceptionWhenAccountNotFound() {
        // given
        String email = "nonexistent@example.com";
        LoginCommand command = new LoginCommand(email, "password");
        when(accountRepository.findByEmail(any(EmailAddress.class))).thenReturn(Optional.empty());

        // when & then
        assertThrows(UnauthorizedException.class, () -> service.login(command));
        verify(authenticator, never()).authenticate(any(), any());
    }

    @Test
    void logout_shouldSucceedWhenSessionExists() {
        // given
        String tokenValue = "valid-token";
        AccessToken token = new AccessToken(tokenValue, Instant.now().plus(1, ChronoUnit.HOURS));
        UserAccountId accountId = UserAccountId.newId();
        UserSession session = UserSession.create(accountId, token);
        LogoutCommand command = new LogoutCommand(tokenValue);

        when(sessionRepository.findByToken(any(AccessToken.class))).thenReturn(Optional.of(session));

        // when
        service.logout(command);

        // then
        assertEquals(SessionStatus.INVALIDATED, session.status());
        verify(sessionRepository).save(session);
        verify(eventPublisher).publish(any(UserLoggedOutEvent.class));
    }

    @Test
    void logout_shouldThrowNotFoundExceptionWhenSessionDoesNotExist() {
        // given
        LogoutCommand command = new LogoutCommand("invalid-token");
        when(sessionRepository.findByToken(any(AccessToken.class))).thenReturn(Optional.empty());

        // when & then
        assertThrows(NotFoundException.class, () -> service.logout(command));
    }

    @Test
    void refresh_shouldSucceedWhenSessionIsValid() {
        // given
        String oldTokenValue = "old-token";
        AccessToken oldToken = new AccessToken(oldTokenValue, Instant.now().plus(1, ChronoUnit.HOURS));
        UserAccountId accountId = UserAccountId.newId();
        UserSession oldSession = UserSession.create(accountId, oldToken);
        RefreshSessionCommand command = new RefreshSessionCommand(oldTokenValue);

        AccessToken newToken = new AccessToken("new-token", Instant.now().plus(1, ChronoUnit.HOURS));

        when(sessionRepository.findByToken(any(AccessToken.class))).thenReturn(Optional.of(oldSession));
        when(tokenGenerator.createAccessToken(accountId)).thenReturn(newToken);
        when(sessionRepository.save(any(UserSession.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        LoginResponse response = service.refresh(command);

        // then
        assertNotNull(response);
        assertEquals(oldTokenValue, oldSession.token().value());
        assertEquals(SessionStatus.INVALIDATED, oldSession.status());
        assertEquals(newToken.value(), response.accessToken());
        verify(sessionRepository, times(2)).save(any(UserSession.class));
    }

    @Test
    void refresh_shouldThrowUnauthorizedExceptionWhenSessionIsInvalid() {
        // given
        String tokenValue = "invalid-token";
        AccessToken token = new AccessToken(tokenValue, Instant.now().plus(1, ChronoUnit.HOURS));
        UserSession session = UserSession.create(UserAccountId.newId(), token);
        session.invalidate();
        RefreshSessionCommand command = new RefreshSessionCommand(tokenValue);

        when(sessionRepository.findByToken(any(AccessToken.class))).thenReturn(Optional.of(session));

        // when & then
        assertThrows(UnauthorizedException.class, () -> service.refresh(command));
    }

    @Test
    void findAccountIdByToken_shouldReturnAccountIdWhenTokenIsValid() {
        // given
        String tokenValue = "valid-token";
        UserAccountId accountId = UserAccountId.newId();
        AccessToken token = new AccessToken(tokenValue, Instant.now().plus(1, ChronoUnit.HOURS));
        UserSession session = UserSession.create(accountId, token);

        when(sessionRepository.findByToken(any(AccessToken.class))).thenReturn(Optional.of(session));

        // when
        Optional<UUID> result = service.findAccountIdByToken(tokenValue);

        // then
        assertTrue(result.isPresent());
        assertEquals(accountId.value(), result.get());
    }

    @Test
    void findAccountIdByToken_shouldReturnEmptyWhenTokenIsInvalidOrMissing() {
        // test null
        assertTrue(service.findAccountIdByToken(null).isEmpty());
        // test blank
        assertTrue(service.findAccountIdByToken("").isEmpty());
        // test nonexistent
        when(sessionRepository.findByToken(any(AccessToken.class))).thenReturn(Optional.empty());
        assertTrue(service.findAccountIdByToken("nonexistent").isEmpty());
    }

    private UserAccount createAccount(String email) {
        return new UserAccount(
                UserAccountId.newId(),
                EmailAddress.of(email),
                new PasswordHash("hashed-password"),
                AccountStatus.ACTIVE,
                Instant.now(),
                Instant.now(),
                null,
                null
        );
    }
}