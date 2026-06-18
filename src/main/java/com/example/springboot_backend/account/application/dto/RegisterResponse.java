package com.example.springboot_backend.account.application.dto;

import java.util.UUID;

public record RegisterResponse(UUID accountId, String email, String verificationTokenForDemo) { }
