package com.example.springboot_backend.account.application.service;

import com.example.springboot_backend.account.application.command.ConfirmEmailCommand;
import com.example.springboot_backend.account.application.command.RegisterUserCommand;
import com.example.springboot_backend.account.application.dto.RegisterResponse;
import com.example.springboot_backend.account.domain.event.AccountActivatedEvent;
import com.example.springboot_backend.account.domain.event.UserRegisteredEvent;
import com.example.springboot_backend.account.domain.model.AccountStatus;
import com.example.springboot_backend.account.domain.model.UserAccount;
import com.example.springboot_backend.account.domain.port.PasswordHasher;
import com.example.springboot_backend.account.domain.port.TokenGenerator;
import com.example.springboot_backend.account.domain.port.VerificationMessageSender;
import com.example.springboot_backend.account.domain.repository.UserAccountRepository;
import com.example.springboot_backend.account.domain.service.PasswordPolicy;
import com.example.springboot_backend.account.domain.valueobject.EmailAddress;
import com.example.springboot_backend.account.domain.valueobject.PasswordHash;
import com.example.springboot_backend.account.domain.valueobject.UserAccountId;
import com.example.springboot_backend.account.domain.valueobject.VerificationToken;
import com.example.springboot_backend.shared.event.DomainEventPublisher;
import com.example.springboot_backend.shared.exception.BusinessException;
import com.example.springboot_backend.shared.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RegistrationApplicationServiceTest {

    private UserAccountRepository accountRepository;
    private PasswordHasher passwordHasher;
    private TokenGenerator tokenGenerator;
    private VerificationMessageSender messageSender;
    private DomainEventPublisher eventPublisher;
    private PasswordPolicy passwordPolicy;
    private RegistrationApplicationService registrationService;

    @BeforeEach
    void setUp() {
        accountRepository = mock(UserAccountRepository.class);
        passwordHasher = mock(PasswordHasher.class);
        tokenGenerator = mock(TokenGenerator.class);
        messageSender = mock(VerificationMessageSender.class);
        eventPublisher = mock(DomainEventPublisher.class);
        passwordPolicy = mock(PasswordPolicy.class);

        registrationService = new RegistrationApplicationService(
                accountRepository, passwordHasher, tokenGenerator, messageSender, eventPublisher, passwordPolicy
        );
    }

    @Test
    void register_ShouldRegisterUserSuccessfully() {
        // Given
        String email = "test@example.com";
        String password = "Password123!";
        RegisterUserCommand command = new RegisterUserCommand(email, password);
        VerificationToken token = new VerificationToken("token123", Instant.now().plus(1, ChronoUnit.HOURS));

        when(accountRepository.existsByEmail(any(EmailAddress.class))).thenReturn(false);
        when(tokenGenerator.createVerificationToken(null)).thenReturn(token);
        when(passwordHasher.hash(password)).thenReturn(new PasswordHash("hashedPassword"));
        when(accountRepository.save(any(UserAccount.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        RegisterResponse response = registrationService.register(command);

        // Then
        assertNotNull(response);
        assertEquals(email, response.email());
        assertEquals("token123", response.verificationTokenForDemo());

        verify(passwordPolicy).check(eq(password), any(EmailAddress.class));
        verify(accountRepository).save(any(UserAccount.class));
        verify(messageSender).sendVerificationLink(eq(EmailAddress.of(email)), eq(token));
        verify(eventPublisher).publish(any(UserRegisteredEvent.class));
    }

    @Test
    void register_ShouldThrowExceptionWhenEmailAlreadyExists() {
        // Given
        String email = "existing@example.com";
        RegisterUserCommand command = new RegisterUserCommand(email, "Password123!");

        when(accountRepository.existsByEmail(any(EmailAddress.class))).thenReturn(true);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> registrationService.register(command));
        assertEquals("E-mail jest już zajęty", exception.getMessage());
        verify(accountRepository, never()).save(any());
    }

    @Test
    void register_ShouldThrowExceptionWhenPasswordPolicyFails() {
        // Given
        String email = "test@example.com";
        String password = "weak";
        RegisterUserCommand command = new RegisterUserCommand(email, password);

        doThrow(new BusinessException("Hasło jest za słabe")).when(passwordPolicy).check(eq(password), any(EmailAddress.class));
        when(accountRepository.existsByEmail(any(EmailAddress.class))).thenReturn(false);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> registrationService.register(command));
        assertEquals("Hasło jest za słabe", exception.getMessage());
        verify(accountRepository, never()).save(any());
    }

    @Test
    void confirmEmail_ShouldActivateAccountSuccessfully() {
        // Given
        String tokenValue = "validToken";
        ConfirmEmailCommand command = new ConfirmEmailCommand(tokenValue);
        VerificationToken token = new VerificationToken(tokenValue, Instant.now().plus(1, ChronoUnit.HOURS));
        UserAccount account = UserAccount.register(EmailAddress.of("test@example.com"), new PasswordHash("hash"), token);

        when(accountRepository.findByVerificationToken(tokenValue)).thenReturn(Optional.of(account));

        // When
        registrationService.confirmEmail(command);

        // Then
        assertEquals(AccountStatus.ACTIVE, account.status());
        assertNull(account.verificationToken());
        verify(accountRepository).save(account);
        verify(eventPublisher).publish(any(AccountActivatedEvent.class));
    }

    @Test
    void confirmEmail_ShouldThrowExceptionWhenTokenNotFound() {
        // Given
        String tokenValue = "invalidToken";
        ConfirmEmailCommand command = new ConfirmEmailCommand(tokenValue);

        when(accountRepository.findByVerificationToken(tokenValue)).thenReturn(Optional.empty());

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class, () -> registrationService.confirmEmail(command));
        assertEquals("Nie znaleziono konta dla tokenu weryfikacyjnego", exception.getMessage());
        verify(eventPublisher, never()).publish(any());
    }

    @Test
    void confirmEmail_ShouldThrowExceptionWhenTokenIsInvalid() {
        // Given
        String tokenValue = "someToken";
        ConfirmEmailCommand command = new ConfirmEmailCommand(tokenValue);
        // Token in account is different than in command
        VerificationToken differentToken = new VerificationToken("differentToken", Instant.now().plus(1, ChronoUnit.HOURS));
        UserAccount account = UserAccount.register(EmailAddress.of("test@example.com"), new PasswordHash("hash"), differentToken);

        when(accountRepository.findByVerificationToken(tokenValue)).thenReturn(Optional.of(account));

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> registrationService.confirmEmail(command));
        assertEquals("Token weryfikacyjny jest niepoprawny albo wygasł", exception.getMessage());
    }
}