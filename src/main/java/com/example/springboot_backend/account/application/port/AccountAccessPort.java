package com.example.springboot_backend.account.application.port;

import com.example.springboot_backend.account.application.dto.UserContactData;
import java.util.Optional;
import java.util.UUID;

public interface AccountAccessPort {
    Optional<UUID> findAccountIdByAccessToken(String accessToken);
    boolean isUserLoggedIn(UUID userId);
    Optional<UserContactData> getUserContactData(UUID userId);
}
