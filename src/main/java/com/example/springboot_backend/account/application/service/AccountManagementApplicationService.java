package com.example.springboot_backend.account.application.service;

import com.example.springboot_backend.account.application.command.BlockAccountCommand;
import com.example.springboot_backend.account.application.command.ChangePasswordCommand;
import com.example.springboot_backend.account.application.command.ChangePhoneNumberCommand;
import com.example.springboot_backend.account.application.command.DeleteAccountCommand;
import com.example.springboot_backend.account.domain.event.AccountBlockedEvent;
import com.example.springboot_backend.account.domain.event.AccountDeletedEvent;
import com.example.springboot_backend.account.domain.event.PasswordChangedEvent;
import com.example.springboot_backend.account.domain.model.UserAccount;
import com.example.springboot_backend.account.domain.port.PasswordHasher;
import com.example.springboot_backend.account.domain.repository.UserAccountRepository;
import com.example.springboot_backend.account.domain.repository.UserSessionRepository;
import com.example.springboot_backend.account.domain.service.PasswordPolicy;
import com.example.springboot_backend.account.domain.valueobject.BlockReason;
import com.example.springboot_backend.account.domain.valueobject.UserAccountId;
import com.example.springboot_backend.shared.event.DomainEventPublisher;
import com.example.springboot_backend.shared.exception.NotFoundException;
import com.example.springboot_backend.shared.exception.UnauthorizedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;

@Service
public class AccountManagementApplicationService {
    private final AuthenticationApplicationService authenticationService;
    private final UserAccountRepository accountRepository;
    private final UserSessionRepository sessionRepository;
    private final PasswordHasher passwordHasher;
    private final PasswordPolicy passwordPolicy;
    private final DomainEventPublisher eventPublisher;

    public AccountManagementApplicationService(AuthenticationApplicationService authenticationService,
                                               UserAccountRepository accountRepository, UserSessionRepository sessionRepository,
                                               PasswordHasher passwordHasher, PasswordPolicy passwordPolicy,
                                               DomainEventPublisher eventPublisher) {
        this.authenticationService = authenticationService;
        this.accountRepository = accountRepository;
        this.sessionRepository = sessionRepository;
        this.passwordHasher = passwordHasher;
        this.passwordPolicy = passwordPolicy;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public void changePassword(ChangePasswordCommand command) {
        var accountId = authenticationService.findAccountIdByToken(command.accessToken())
                .orElseThrow(() -> new UnauthorizedException("Brak poprawnej sesji"));
        UserAccount account = accountRepository.findById(UserAccountId.of(accountId))
                .orElseThrow(() -> new NotFoundException("Konto nie istnieje"));
        if (!passwordHasher.matches(command.oldPassword(), account.passwordHash())) {
            throw new UnauthorizedException("Stare hasło jest niepoprawne");
        }
        passwordPolicy.check(command.newPassword(), account.email());
        account.changePassword(passwordHasher.hash(command.newPassword()));
        accountRepository.save(account);
        sessionRepository.invalidateSessionsOfAccount(account.id());
        eventPublisher.publish(new PasswordChangedEvent(account.id().value(), Instant.now()));
    }

    @Transactional
    public void changePhoneNumber(ChangePhoneNumberCommand command) {
        var accountId = authenticationService.findAccountIdByToken(command.accessToken())
                .orElseThrow(() -> new UnauthorizedException("Brak poprawnej sesji"));
        UserAccount account = accountRepository.findById(UserAccountId.of(accountId))
                .orElseThrow(() -> new NotFoundException("Konto nie istnieje"));
        account.changePhoneNumber(command.phoneNumber());
        accountRepository.save(account);
    }

    @Transactional
    public void block(BlockAccountCommand command) {
        UserAccount account = accountRepository.findById(UserAccountId.of(command.accountId()))
                .orElseThrow(() -> new NotFoundException("Konto nie istnieje"));
        account.block(new BlockReason(command.reason()));
        accountRepository.save(account);
        sessionRepository.invalidateSessionsOfAccount(account.id());
        eventPublisher.publish(new AccountBlockedEvent(account.id().value(), command.reason(), Instant.now()));
    }

    @Transactional
    public void delete(DeleteAccountCommand command) {
        var accountId = authenticationService.findAccountIdByToken(command.accessToken())
                .orElseThrow(() -> new UnauthorizedException("Brak poprawnej sesji"));
        UserAccount account = accountRepository.findById(UserAccountId.of(accountId))
                .orElseThrow(() -> new NotFoundException("Konto nie istnieje"));
        account.delete();
        accountRepository.save(account);
        sessionRepository.invalidateSessionsOfAccount(account.id());
        eventPublisher.publish(new AccountDeletedEvent(account.id().value(), Instant.now()));
    }
}
