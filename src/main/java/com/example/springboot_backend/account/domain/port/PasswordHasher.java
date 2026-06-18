package com.example.springboot_backend.account.domain.port;

import com.example.springboot_backend.account.domain.valueobject.PasswordHash;

public interface PasswordHasher {
    PasswordHash hash(String rawPassword);
    boolean matches(String rawPassword, PasswordHash passwordHash);
}
