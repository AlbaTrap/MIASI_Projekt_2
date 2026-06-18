package com.example.springboot_backend.event.domain.valueobject;
import com.example.springboot_backend.shared.exception.BusinessException;
public record EventTitle(String value) {
    public EventTitle {
        if (value == null || value.isBlank()) throw new BusinessException("Tytuł wydarzenia nie może być pusty");
        value = value.trim();
    }
}
