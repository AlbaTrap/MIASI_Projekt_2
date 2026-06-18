package com.example.springboot_backend.account.domain.valueobject;

import com.example.springboot_backend.shared.exception.BusinessException;

public record PasswordHash(String value) {
    public PasswordHash {
        if (value == null || value.isBlank()) throw new BusinessException("Skrót hasła nie może być pusty");
    }
    public static PasswordHash of(String value) { return new PasswordHash(value); }
}
