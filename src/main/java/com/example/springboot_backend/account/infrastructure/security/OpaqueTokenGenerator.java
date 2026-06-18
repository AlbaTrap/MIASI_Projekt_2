package com.example.springboot_backend.account.infrastructure.security;

import com.example.springboot_backend.account.domain.port.TokenGenerator;
import com.example.springboot_backend.account.domain.valueobject.AccessToken;
import com.example.springboot_backend.account.domain.valueobject.UserAccountId;
import com.example.springboot_backend.account.domain.valueobject.VerificationToken;
import org.springframework.stereotype.Component;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;

@Component
public class OpaqueTokenGenerator implements TokenGenerator {
    private final SecureRandom secureRandom = new SecureRandom();

    @Override
    public AccessToken createAccessToken(UserAccountId accountId) {
        return new AccessToken(randomToken(48), Instant.now().plusSeconds(60 * 60));
    }

    @Override
    public VerificationToken createVerificationToken(UserAccountId accountId) {
        return new VerificationToken(randomToken(32), Instant.now().plusSeconds(24 * 60 * 60));
    }

    private String randomToken(int bytes) {
        byte[] data = new byte[bytes];
        secureRandom.nextBytes(data);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(data);
    }
}
