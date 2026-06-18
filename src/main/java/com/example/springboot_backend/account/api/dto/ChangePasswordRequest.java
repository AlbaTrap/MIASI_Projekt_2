package com.example.springboot_backend.account.api.dto;

import jakarta.validation.constraints.NotBlank;

public record ChangePasswordRequest(@NotBlank String oldPassword, @NotBlank String newPassword) { }
