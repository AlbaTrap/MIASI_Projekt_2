package com.example.springboot_backend.account.application.dto;

import com.example.springboot_backend.account.domain.model.AccountStatus;
import java.time.Instant;
import java.util.UUID;

public record UserDto(UUID id, String email, AccountStatus status, Instant registeredAt, Instant activatedAt,
                      String phoneNumber) { }
