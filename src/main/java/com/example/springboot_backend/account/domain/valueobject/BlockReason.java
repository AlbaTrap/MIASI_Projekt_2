package com.example.springboot_backend.account.domain.valueobject;

public record BlockReason(String value) {
    public BlockReason {
        if (value == null || value.isBlank()) value = "Brak podanego powodu";
    }
}
