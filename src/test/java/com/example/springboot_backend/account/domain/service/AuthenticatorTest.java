package com.example.springboot_backend.account.domain.service;

import com.example.springboot_backend.account.domain.model.AccountStatus;
import com.example.springboot_backend.account.domain.model.UserAccount;
import com.example.springboot_backend.account.domain.port.PasswordHasher;
import com.example.springboot_backend.account.domain.valueobject.EmailAddress;
import com.example.springboot_backend.account.domain.valueobject.PasswordHash;
import com.example.springboot_backend.account.domain.valueobject.UserAccountId;
import com.example.springboot_backend.shared.exception.UnauthorizedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticatorTest {

    @Mock
    private PasswordHasher passwordHasher;

    private Authenticator authenticator;

    @BeforeEach
    void setUp() {
        authenticator = new Authenticator(passwordHasher);
    }

    @Test
    void shouldAuthenticateSuccessfullyWhenAccountIsActiveAndPasswordMatches() {
        // given
        UserAccount account = createAccount(AccountStatus.ACTIVE, "hashed-password");
        String rawPassword = "password123";
        when(passwordHasher.matches(rawPassword, account.passwordHash())).thenReturn(true);

        // when & then
        assertDoesNotThrow(() -> authenticator.authenticate(account, rawPassword));
        verify(passwordHasher).matches(rawPassword, account.passwordHash());
    }

    @Test
    void shouldThrowUnauthorizedExceptionWhenAccountIsNotActive() {
        // given
        UserAccount account = createAccount(AccountStatus.PENDING_CONFIRMATION, "hashed-password");
        String rawPassword = "password123";

        // when & then
        UnauthorizedException exception = assertThrows(UnauthorizedException.class,
                () -> authenticator.authenticate(account, rawPassword));
        assertEquals("Konto nie jest aktywne", exception.getMessage());
        verifyNoInteractions(passwordHasher);
    }

    @Test
    void shouldThrowUnauthorizedExceptionWhenAccountIsBlocked() {
        // given
        UserAccount account = createAccount(AccountStatus.BLOCKED, "hashed-password");
        String rawPassword = "password123";

        // when & then
        UnauthorizedException exception = assertThrows(UnauthorizedException.class,
                () -> authenticator.authenticate(account, rawPassword));
        assertEquals("Konto nie jest aktywne", exception.getMessage());
        verifyNoInteractions(passwordHasher);
    }

    @Test
    void shouldThrowUnauthorizedExceptionWhenPasswordDoesNotMatch() {
        // given
        UserAccount account = createAccount(AccountStatus.ACTIVE, "hashed-password");
        String rawPassword = "wrong-password";
        when(passwordHasher.matches(rawPassword, account.passwordHash())).thenReturn(false);

        // when & then
        UnauthorizedException exception = assertThrows(UnauthorizedException.class,
                () -> authenticator.authenticate(account, rawPassword));
        assertEquals("Błędny e-mail albo hasło", exception.getMessage());
        verify(passwordHasher).matches(rawPassword, account.passwordHash());
    }

    private UserAccount createAccount(AccountStatus status, String passwordHash) {
        return new UserAccount(
                UserAccountId.newId(),
                new EmailAddress("test@example.com"),
                new PasswordHash(passwordHash),
                status,
                Instant.now(),
                status == AccountStatus.ACTIVE ? Instant.now() : null,
                null,
                null
        );
    }
}