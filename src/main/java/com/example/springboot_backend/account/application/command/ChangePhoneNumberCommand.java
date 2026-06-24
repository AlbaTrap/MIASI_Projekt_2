package com.example.springboot_backend.account.application.command;

public record ChangePhoneNumberCommand(String accessToken, String phoneNumber) { }
