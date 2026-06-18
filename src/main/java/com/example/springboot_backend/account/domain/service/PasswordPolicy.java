package com.example.springboot_backend.account.domain.service;

import com.example.springboot_backend.account.domain.valueobject.EmailAddress;
import com.example.springboot_backend.shared.exception.BusinessException;
import org.springframework.stereotype.Component;

@Component
public class PasswordPolicy {
    public void check(String rawPassword, EmailAddress email) {
        if (rawPassword == null || rawPassword.isBlank()) throw new BusinessException("Hasło nie może być puste");
        if (rawPassword.length() < 8) throw new BusinessException("Hasło musi mieć co najmniej 8 znaków");
        if (!rawPassword.matches(".*[A-Za-z].*")) throw new BusinessException("Hasło musi zawierać literę");
        if (!rawPassword.matches(".*[0-9].*")) throw new BusinessException("Hasło musi zawierać cyfrę");
        if (email != null && rawPassword.equalsIgnoreCase(email.value())) throw new BusinessException("Hasło nie może być takie samo jak e-mail");
    }
}
