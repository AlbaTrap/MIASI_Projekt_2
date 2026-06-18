package com.example.springboot_backend.notification.domain.valueobject;
import com.example.springboot_backend.shared.exception.BusinessException;
public record NotificationContent(String subject, String message) {
    public NotificationContent {
        if (subject == null || subject.isBlank()) throw new BusinessException("Temat powiadomienia jest wymagany");
        if (message == null || message.isBlank()) throw new BusinessException("Treść powiadomienia jest wymagana");
    }
}
