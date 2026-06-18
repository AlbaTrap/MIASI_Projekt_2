package com.example.springboot_backend.account.domain.port;

import com.example.springboot_backend.account.domain.valueobject.EmailAddress;
import com.example.springboot_backend.account.domain.valueobject.VerificationToken;

public interface VerificationMessageSender {
    void sendVerificationLink(EmailAddress email, VerificationToken token);
}
