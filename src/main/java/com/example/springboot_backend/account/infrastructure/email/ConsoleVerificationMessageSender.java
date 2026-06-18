package com.example.springboot_backend.account.infrastructure.email;

import com.example.springboot_backend.account.domain.port.VerificationMessageSender;
import com.example.springboot_backend.account.domain.valueobject.EmailAddress;
import com.example.springboot_backend.account.domain.valueobject.VerificationToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ConsoleVerificationMessageSender implements VerificationMessageSender {
    private static final Logger log = LoggerFactory.getLogger(ConsoleVerificationMessageSender.class);
    @Override
    public void sendVerificationLink(EmailAddress email, VerificationToken token) {
        log.info("[DEMO MAIL] Link aktywacyjny dla {}: http://localhost:8080/api/auth/confirm?token={}", email.value(), token.value());
    }
}
