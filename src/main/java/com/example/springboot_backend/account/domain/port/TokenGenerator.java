package com.example.springboot_backend.account.domain.port;

import com.example.springboot_backend.account.domain.valueobject.AccessToken;
import com.example.springboot_backend.account.domain.valueobject.UserAccountId;
import com.example.springboot_backend.account.domain.valueobject.VerificationToken;

public interface TokenGenerator {
    AccessToken createAccessToken(UserAccountId accountId);
    VerificationToken createVerificationToken(UserAccountId accountId);
}
