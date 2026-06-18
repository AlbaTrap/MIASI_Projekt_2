package com.example.springboot_backend.account.application.dto;

import java.util.UUID;

public record UserContactData(UUID userId, String email, String phoneNumber) { }
