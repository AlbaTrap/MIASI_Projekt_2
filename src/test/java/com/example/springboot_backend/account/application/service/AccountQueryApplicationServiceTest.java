package com.example.springboot_backend.account.application.service;

import com.example.springboot_backend.account.application.dto.UserContactData;
import com.example.springboot_backend.account.application.dto.UserDto;
import com.example.springboot_backend.account.domain.model.AccountStatus;
import com.example.springboot_backend.account.domain.model.UserAccount;
import com.example.springboot_backend.account.domain.repository.UserAccountRepository;
import com.example.springboot_backend.account.domain.valueobject.EmailAddress;
import com.example.springboot_backend.account.domain.valueobject.PasswordHash;
import com.example.springboot_backend.account.domain.valueobject.UserAccountId;
import com.example.springboot_backend.shared.exception.NotFoundException;
import com.example.springboot_backend.shared.exception.UnauthorizedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AccountQueryApplicationServiceTest {

    private AuthenticationApplicationService authenticationService;
    private UserAccountRepository accountRepository;
    private AccountQueryApplicationService service;

    @BeforeEach
    void setUp() {
        authenticationService = mock(AuthenticationApplicationService.class);
        accountRepository = mock(UserAccountRepository.class);
        service = new AccountQueryApplicationService(authenticationService, accountRepository);
    }

    @Test
    void me_Success() {
        // given
        String token = "valid-token";
        UUID userId = UUID.randomUUID();
        UserAccountId accountId = UserAccountId.of(userId);
        UserAccount account = createAccount(accountId, "test@example.com", AccountStatus.ACTIVE);

        when(authenticationService.findAccountIdByToken(token)).thenReturn(Optional.of(userId));
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        // when
        UserDto result = service.me(token);

        // then
        assertNotNull(result);
        assertEquals(userId, result.id());
        assertEquals("test@example.com", result.email());
        assertEquals(AccountStatus.ACTIVE, result.status());
    }

    @Test
    void me_Unauthorized_NoSession() {
        // given
        String token = "invalid-token";
        when(authenticationService.findAccountIdByToken(token)).thenReturn(Optional.empty());

        // when & then
        assertThrows(UnauthorizedException.class, () -> service.me(token));
    }

    @Test
    void me_NotFound_AccountMissing() {
        // given
        String token = "valid-token";
        UUID userId = UUID.randomUUID();
        when(authenticationService.findAccountIdByToken(token)).thenReturn(Optional.of(userId));
        when(accountRepository.findById(any())).thenReturn(Optional.empty());

        // when & then
        assertThrows(NotFoundException.class, () -> service.me(token));
    }

    @Test
    void findAccountIdByAccessToken() {
        // given
        String token = "some-token";
        UUID userId = UUID.randomUUID();
        when(authenticationService.findAccountIdByToken(token)).thenReturn(Optional.of(userId));

        // when
        Optional<UUID> result = service.findAccountIdByAccessToken(token);

        // then
        assertTrue(result.isPresent());
        assertEquals(userId, result.get());
        verify(authenticationService).findAccountIdByToken(token);
    }

    @Test
    void isUserLoggedIn_Active() {
        // given
        UUID userId = UUID.randomUUID();
        UserAccountId accountId = UserAccountId.of(userId);
        UserAccount account = createAccount(accountId, "test@example.com", AccountStatus.ACTIVE);
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        // when
        boolean result = service.isUserLoggedIn(userId);

        // then
        assertTrue(result);
    }

    @Test
    void isUserLoggedIn_Inactive() {
        // given
        UUID userId = UUID.randomUUID();
        UserAccountId accountId = UserAccountId.of(userId);
        UserAccount account = createAccount(accountId, "test@example.com", AccountStatus.PENDING_CONFIRMATION);
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        // when
        boolean result = service.isUserLoggedIn(userId);

        // then
        assertFalse(result);
    }

    @Test
    void isUserLoggedIn_NotFound() {
        // given
        UUID userId = UUID.randomUUID();
        when(accountRepository.findById(any())).thenReturn(Optional.empty());

        // when
        boolean result = service.isUserLoggedIn(userId);

        // then
        assertFalse(result);
    }

    @Test
    void getUserContactData_Success() {
        // given
        UUID userId = UUID.randomUUID();
        UserAccountId accountId = UserAccountId.of(userId);
        UserAccount account = createAccount(accountId, "test@example.com", AccountStatus.ACTIVE);
        account.changePhoneNumber("123456789");
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        // when
        Optional<UserContactData> result = service.getUserContactData(userId);

        // then
        assertTrue(result.isPresent());
        assertEquals("test@example.com", result.get().email());
        assertEquals("123456789", result.get().phoneNumber());
    }

    @Test
    void getUserContactData_Inactive() {
        // given
        UUID userId = UUID.randomUUID();
        UserAccountId accountId = UserAccountId.of(userId);
        UserAccount account = createAccount(accountId, "test@example.com", AccountStatus.BLOCKED);
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        // when
        Optional<UserContactData> result = service.getUserContactData(userId);

        // then
        assertTrue(result.isEmpty());
    }

    private UserAccount createAccount(UserAccountId id, String email, AccountStatus status) {
        return new UserAccount(
                id,
                new EmailAddress(email),
                new PasswordHash("hash"),
                status,
                Instant.now(),
                status == AccountStatus.ACTIVE ? Instant.now() : null,
                null,
                null
        );
    }
}