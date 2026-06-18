package com.example.springboot_backend.account.application.service;

import com.example.springboot_backend.account.application.command.ConfirmEmailCommand;
import com.example.springboot_backend.account.application.command.RegisterUserCommand;
import com.example.springboot_backend.account.application.dto.RegisterResponse;
import com.example.springboot_backend.account.domain.event.AccountActivatedEvent;
import com.example.springboot_backend.account.domain.event.UserRegisteredEvent;
import com.example.springboot_backend.account.domain.model.UserAccount;
import com.example.springboot_backend.account.domain.port.PasswordHasher;
import com.example.springboot_backend.account.domain.port.TokenGenerator;
import com.example.springboot_backend.account.domain.port.VerificationMessageSender;
import com.example.springboot_backend.account.domain.repository.UserAccountRepository;
import com.example.springboot_backend.account.domain.service.PasswordPolicy;
import com.example.springboot_backend.account.domain.valueobject.EmailAddress;
import com.example.springboot_backend.account.domain.valueobject.VerificationToken;
import com.example.springboot_backend.shared.event.DomainEventPublisher;
import com.example.springboot_backend.shared.exception.BusinessException;
import com.example.springboot_backend.shared.exception.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;

@Service
public class RegistrationApplicationService {
    private final UserAccountRepository accountRepository;
    private final PasswordHasher passwordHasher;
    private final TokenGenerator tokenGenerator;
    private final VerificationMessageSender messageSender;
    private final DomainEventPublisher eventPublisher;
    private final PasswordPolicy passwordPolicy;

    public RegistrationApplicationService(UserAccountRepository accountRepository, PasswordHasher passwordHasher,
                                          TokenGenerator tokenGenerator, VerificationMessageSender messageSender,
                                          DomainEventPublisher eventPublisher, PasswordPolicy passwordPolicy) {
        this.accountRepository = accountRepository;
        this.passwordHasher = passwordHasher;
        this.tokenGenerator = tokenGenerator;
        this.messageSender = messageSender;
        this.eventPublisher = eventPublisher;
        this.passwordPolicy = passwordPolicy;
    }

    @Transactional
    public RegisterResponse register(RegisterUserCommand command) {
        EmailAddress email = EmailAddress.of(command.email());
        if (accountRepository.existsByEmail(email)) throw new BusinessException("E-mail jest już zajęty");
        passwordPolicy.check(command.password(), email);
        VerificationToken token = tokenGenerator.createVerificationToken(null);
        UserAccount account = UserAccount.register(email, passwordHasher.hash(command.password()), token);
        UserAccount saved = accountRepository.save(account);
        messageSender.sendVerificationLink(saved.email(), token);
        eventPublisher.publish(new UserRegisteredEvent(saved.id().value(), saved.email().value(), Instant.now()));
        return new RegisterResponse(saved.id().value(), saved.email().value(), token.value());
    }

    @Transactional
    public void confirmEmail(ConfirmEmailCommand command) {
        UserAccount account = accountRepository.findByVerificationToken(command.token())
                .orElseThrow(() -> new NotFoundException("Nie znaleziono konta dla tokenu weryfikacyjnego"));
        account.confirmEmail(command.token());
        accountRepository.save(account);
        eventPublisher.publish(new AccountActivatedEvent(account.id().value(), Instant.now()));
    }
}
