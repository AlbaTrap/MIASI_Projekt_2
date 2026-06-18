package com.example.springboot_backend.account.domain.valueobject;

import com.example.springboot_backend.shared.exception.BusinessException;

public record EmailAddress(String value) {
    public EmailAddress {
        if (value == null || value.isBlank()) throw new BusinessException("Adres e-mail nie może być pusty");
        value = value.trim().toLowerCase();
        if (!value.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new BusinessException("Adres e-mail ma niepoprawny format");
        }
    }
    public static EmailAddress of(String value) { return new EmailAddress(value); }
}
