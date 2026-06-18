package com.example.springboot_backend.account.mapper;

import com.example.springboot_backend.account.application.dto.UserDto;
import com.example.springboot_backend.account.domain.model.UserAccount;

public final class UserMapper {
    private UserMapper() {}

    public static UserDto toDto(UserAccount account) {
        return new UserDto(account.id().value(), account.email().value(), account.status(), account.registeredAt(), account.activatedAt());
    }
}
