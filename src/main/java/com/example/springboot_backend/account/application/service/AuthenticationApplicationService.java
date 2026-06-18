package com.example.springboot_backend.account.application.service;

import com.example.springboot_backend.account.application.command.LoginCommand;
import com.example.springboot_backend.account.application.command.LogoutCommand;
import com.example.springboot_backend.account.application.command.RefreshSessionCommand;
import com.example.springboot_backend.account.application.dto.LoginResponse;
import com.example.springboot_backend.account.domain.event.UserLoggedInEvent;
import com.example.springboot_backend.account.domain.event.UserLoggedOutEvent;
import com.example.springboot_backend.account.domain.model.UserAccount;
import com.example.springboot_backend.account.domain.model.UserSession;
import com.example.springboot_backend.account.domain.port.TokenGenerator;
import com.example.springboot_backend.account.domain.repository.UserAccountRepository;
import com.example.springboot_backend.account.domain.repository.UserSessionRepository;
import com.example.springboot_backend.account.domain.service.Authenticator;
import com.example.springboot_backend.account.domain.valueobject.AccessToken;
import com.example.springboot_backend.account.domain.valueobject.EmailAddress;
import com.example.springboot_backend.shared.event.DomainEventPublisher;
import com.example.springboot_backend.shared.exception.NotFoundException;
import com.example.springboot_backend.shared.exception.UnauthorizedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthenticationApplicationService {
    private final UserAccountRepository accountRepository;
    private final UserSessionRepository sessionRepository;
    private final TokenGenerator tokenGenerator;
    private final Authenticator authenticator;
    private final DomainEventPublisher eventPublisher;

    public AuthenticationApplicationService(UserAccountRepository accountRepository, UserSessionRepository sessionRepository,
                                            TokenGenerator tokenGenerator, Authenticator authenticator,
                                            DomainEventPublisher eventPublisher) {
        this.accountRepository = accountRepository;
        this.sessionRepository = sessionRepository;
        this.tokenGenerator = tokenGenerator;
        this.authenticator = authenticator;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public LoginResponse login(LoginCommand command) {
        UserAccount account = accountRepository.findByEmail(EmailAddress.of(command.email()))
                .orElseThrow(() -> new UnauthorizedException("Błędny e-mail albo hasło"));
        authenticator.authenticate(account, command.password());
        AccessToken token = tokenGenerator.createAccessToken(account.id());
        UserSession session = sessionRepository.save(UserSession.create(account.id(), token));
        eventPublisher.publish(new UserLoggedInEvent(account.id().value(), session.id().value(), Instant.now()));
        return new LoginResponse(account.id().value(), token.value(), token.expiresAt());
    }

    @Transactional
    public void logout(LogoutCommand command) {
        UserSession session = sessionRepository.findByToken(new AccessToken(command.accessToken(), Instant.MAX))
                .orElseThrow(() -> new NotFoundException("Sesja nie istnieje"));
        session.invalidate();
        sessionRepository.save(session);
        eventPublisher.publish(new UserLoggedOutEvent(session.accountId().value(), session.id().value(), Instant.now()));
    }

    @Transactional
    public LoginResponse refresh(RefreshSessionCommand command) {
        UserSession oldSession = sessionRepository.findByToken(new AccessToken(command.accessToken(), Instant.MAX))
                .orElseThrow(() -> new UnauthorizedException("Sesja nie istnieje"));
        if (!oldSession.valid()) throw new UnauthorizedException("Sesja jest nieważna");
        oldSession.invalidate();
        sessionRepository.save(oldSession);
        AccessToken newToken = tokenGenerator.createAccessToken(oldSession.accountId());
        UserSession newSession = sessionRepository.save(UserSession.create(oldSession.accountId(), newToken));
        return new LoginResponse(newSession.accountId().value(), newToken.value(), newToken.expiresAt());
    }

    @Transactional(readOnly = true)
    public Optional<UUID> findAccountIdByToken(String token) {
        if (token == null || token.isBlank()) return Optional.empty();
        return sessionRepository.findByToken(new AccessToken(token, Instant.MAX))
                .filter(UserSession::valid)
                .map(s -> s.accountId().value());
    }
}
