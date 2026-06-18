package com.example.springboot_backend.account.infrastructure.security;

import com.example.springboot_backend.account.domain.port.PasswordHasher;
import com.example.springboot_backend.account.domain.valueobject.PasswordHash;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class BCryptPasswordHasher implements PasswordHasher {
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    @Override public PasswordHash hash(String rawPassword) { return PasswordHash.of(encoder.encode(rawPassword)); }
    @Override public boolean matches(String rawPassword, PasswordHash passwordHash) { return encoder.matches(rawPassword, passwordHash.value()); }
}
