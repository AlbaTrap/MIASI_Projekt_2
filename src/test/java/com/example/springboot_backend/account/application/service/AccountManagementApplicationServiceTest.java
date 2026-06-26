package com.example.springboot_backend.account.application.service;

import com.example.springboot_backend.account.application.command.BlockAccountCommand;
import com.example.springboot_backend.account.application.command.ChangePasswordCommand;
import com.example.springboot_backend.account.application.command.ChangePhoneNumberCommand;
import com.example.springboot_backend.account.application.command.DeleteAccountCommand;
import com.example.springboot_backend.account.domain.event.AccountBlockedEvent;
import com.example.springboot_backend.account.domain.event.AccountDeletedEvent;
import com.example.springboot_backend.account.domain.event.PasswordChangedEvent;
import com.example.springboot_backend.account.domain.model.AccountStatus;
import com.example.springboot_backend.account.domain.model.UserAccount;
import com.example.springboot_backend.account.domain.port.PasswordHasher;
import com.example.springboot_backend.account.domain.repository.UserAccountRepository;
import com.example.springboot_backend.account.domain.repository.UserSessionRepository;
import com.example.springboot_backend.account.domain.service.PasswordPolicy;
import com.example.springboot_backend.account.domain.valueobject.EmailAddress;
import com.example.springboot_backend.account.domain.valueobject.PasswordHash;
import com.example.springboot_backend.account.domain.valueobject.UserAccountId;
import com.example.springboot_backend.account.domain.valueobject.VerificationToken;
import com.example.springboot_backend.shared.event.DomainEventPublisher;
import com.example.springboot_backend.shared.exception.NotFoundException;
import com.example.springboot_backend.shared.exception.UnauthorizedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AccountManagementApplicationServiceTest {

    private AuthenticationApplicationService authenticationService;
    private UserAccountRepository accountRepository;
    private UserSessionRepository sessionRepository;
    private PasswordHasher passwordHasher;
    private PasswordPolicy passwordPolicy;
    private DomainEventPublisher eventPublisher;
    private AccountManagementApplicationService service;

    @BeforeEach
    void setUp() {
        authenticationService = mock(AuthenticationApplicationService.class);
        accountRepository = mock(UserAccountRepository.class);
        sessionRepository = mock(UserSessionRepository.class);
        passwordHasher = mock(PasswordHasher.class);
        passwordPolicy = mock(PasswordPolicy.class);
        eventPublisher = mock(DomainEventPublisher.class);
        service = new AccountManagementApplicationService(
                authenticationService,
                accountRepository,
                sessionRepository,
                passwordHasher,
                passwordPolicy,
                eventPublisher
        );
    }

    @Test
    void changePassword_Success() {
        // given
        String token = "valid-token";
        UUID userId = UUID.randomUUID();
        UserAccountId accountId = UserAccountId.of(userId);
        ChangePasswordCommand command = new ChangePasswordCommand(token, "old-pass", "new-pass");
        UserAccount account = createAccount(accountId, "test@example.com", "hashed-old-pass");

        when(authenticationService.findAccountIdByToken(token)).thenReturn(Optional.of(userId));
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(passwordHasher.matches("old-pass", account.passwordHash())).thenReturn(true);
        when(passwordHasher.hash("new-pass")).thenReturn(new PasswordHash("hashed-new-pass"));

        // when
        service.changePassword(command);

        // then
        assertEquals("hashed-new-pass", account.passwordHash().value());
        verify(accountRepository).save(account);
        verify(sessionRepository).invalidateSessionsOfAccount(accountId);
        verify(eventPublisher).publish(any(PasswordChangedEvent.class));
        verify(passwordPolicy).check("new-pass", account.email());
    }

    @Test
    void changePassword_Unauthorized_NoSession() {
        // given
        ChangePasswordCommand command = new ChangePasswordCommand("invalid-token", "old", "new");
        when(authenticationService.findAccountIdByToken(any())).thenReturn(Optional.empty());

        // when & then
        assertThrows(UnauthorizedException.class, () -> service.changePassword(command));
    }

    @Test
    void changePassword_NotFound_AccountMissing() {
        // given
        String token = "valid-token";
        UUID userId = UUID.randomUUID();
        ChangePasswordCommand command = new ChangePasswordCommand(token, "old", "new");

        when(authenticationService.findAccountIdByToken(token)).thenReturn(Optional.of(userId));
        when(accountRepository.findById(any())).thenReturn(Optional.empty());

        // when & then
        assertThrows(NotFoundException.class, () -> service.changePassword(command));
    }

    @Test
    void changePassword_Unauthorized_WrongOldPassword() {
        // given
        String token = "valid-token";
        UUID userId = UUID.randomUUID();
        UserAccountId accountId = UserAccountId.of(userId);
        ChangePasswordCommand command = new ChangePasswordCommand(token, "wrong-old", "new");
        UserAccount account = createAccount(accountId, "test@example.com", "hashed-old");

        when(authenticationService.findAccountIdByToken(token)).thenReturn(Optional.of(userId));
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(passwordHasher.matches("wrong-old", account.passwordHash())).thenReturn(false);

        // when & then
        assertThrows(UnauthorizedException.class, () -> service.changePassword(command));
    }

    @Test
    void changePhoneNumber_Success() {
        // given
        String token = "valid-token";
        UUID userId = UUID.randomUUID();
        UserAccountId accountId = UserAccountId.of(userId);
        ChangePhoneNumberCommand command = new ChangePhoneNumberCommand(token, "123456789");
        UserAccount account = createAccount(accountId, "test@example.com", "hash");

        when(authenticationService.findAccountIdByToken(token)).thenReturn(Optional.of(userId));
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        // when
        service.changePhoneNumber(command);

        // then
        assertEquals("123456789", account.phoneNumber());
        verify(accountRepository).save(account);
    }

    @Test
    void block_Success() {
        // given
        UUID userId = UUID.randomUUID();
        UserAccountId accountId = UserAccountId.of(userId);
        BlockAccountCommand command = new BlockAccountCommand(userId, "Spam");
        UserAccount account = createAccount(accountId, "test@example.com", "hash");

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        // when
        service.block(command);

        // then
        assertEquals(AccountStatus.BLOCKED, account.status());
        verify(accountRepository).save(account);
        verify(sessionRepository).invalidateSessionsOfAccount(accountId);
        
        ArgumentCaptor<AccountBlockedEvent> eventCaptor = ArgumentCaptor.forClass(AccountBlockedEvent.class);
        verify(eventPublisher).publish(eventCaptor.capture());
        assertEquals(userId, eventCaptor.getValue().accountId());
        assertEquals("Spam", eventCaptor.getValue().reason());
    }

    @Test
    void delete_Success() {
        // given
        String token = "valid-token";
        UUID userId = UUID.randomUUID();
        UserAccountId accountId = UserAccountId.of(userId);
        DeleteAccountCommand command = new DeleteAccountCommand(token);
        UserAccount account = createAccount(accountId, "test@example.com", "hash");

        when(authenticationService.findAccountIdByToken(token)).thenReturn(Optional.of(userId));
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        // when
        service.delete(command);

        // then
        assertEquals(AccountStatus.DELETED, account.status());
        verify(accountRepository).save(account);
        verify(sessionRepository).invalidateSessionsOfAccount(accountId);
        verify(eventPublisher).publish(any(AccountDeletedEvent.class));
    }

    private UserAccount createAccount(UserAccountId id, String email, String passwordHash) {
        return new UserAccount(
                id,
                EmailAddress.of(email),
                new PasswordHash(passwordHash),
                AccountStatus.ACTIVE,
                Instant.now(),
                Instant.now(),
                null,
                null
        );
    }
}