package com.example.springboot_backend.account.application.service;

import com.example.springboot_backend.account.application.dto.UserContactData;
import com.example.springboot_backend.account.application.dto.UserDto;
import com.example.springboot_backend.account.application.port.AccountAccessPort;
import com.example.springboot_backend.account.domain.repository.UserAccountRepository;
import com.example.springboot_backend.account.domain.valueobject.UserAccountId;
import com.example.springboot_backend.account.mapper.UserMapper;
import com.example.springboot_backend.shared.exception.NotFoundException;
import com.example.springboot_backend.shared.exception.UnauthorizedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import java.util.UUID;

@Service
public class AccountQueryApplicationService implements AccountAccessPort {
    private final AuthenticationApplicationService authenticationService;
    private final UserAccountRepository accountRepository;

    public AccountQueryApplicationService(AuthenticationApplicationService authenticationService, UserAccountRepository accountRepository) {
        this.authenticationService = authenticationService;
        this.accountRepository = accountRepository;
    }

    @Transactional(readOnly = true)
    public UserDto me(String accessToken) {
        UUID id = findAccountIdByAccessToken(accessToken).orElseThrow(() -> new UnauthorizedException("Brak poprawnej sesji"));
        return accountRepository.findById(UserAccountId.of(id)).map(UserMapper::toDto)
                .orElseThrow(() -> new NotFoundException("Konto nie istnieje"));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UUID> findAccountIdByAccessToken(String accessToken) {
        return authenticationService.findAccountIdByToken(accessToken);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isUserLoggedIn(UUID userId) {
        return accountRepository.findById(UserAccountId.of(userId)).map(a -> a.active()).orElse(false);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserContactData> getUserContactData(UUID userId) {
        return accountRepository.findById(UserAccountId.of(userId))
                .filter(a -> a.active())
                .map(a -> new UserContactData(a.id().value(), a.email().value(), a.phoneNumber()));
    }
}
