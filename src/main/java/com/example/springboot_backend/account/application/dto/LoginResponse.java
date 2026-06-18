package com.example.springboot_backend.account.application.dto;

import java.time.Instant;
import java.util.UUID;

public record LoginResponse(UUID accountId, String accessToken, Instant expiresAt) { }
