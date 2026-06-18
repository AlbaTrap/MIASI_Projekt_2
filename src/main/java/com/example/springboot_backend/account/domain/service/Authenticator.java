package com.example.springboot_backend.account.domain.service;

import com.example.springboot_backend.account.domain.model.UserAccount;
import com.example.springboot_backend.account.domain.port.PasswordHasher;
import com.example.springboot_backend.shared.exception.UnauthorizedException;
import org.springframework.stereotype.Component;

@Component
public class Authenticator {
    private final PasswordHasher passwordHasher;

    public Authenticator(PasswordHasher passwordHasher) {
        this.passwordHasher = passwordHasher;
    }

    public void authenticate(UserAccount account, String rawPassword) {
        if (!account.active()) throw new UnauthorizedException("Konto nie jest aktywne");
        if (!passwordHasher.matches(rawPassword, account.passwordHash())) throw new UnauthorizedException("Błędny e-mail albo hasło");
    }
}
