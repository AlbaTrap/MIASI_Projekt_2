package com.example.springboot_backend.account.application.command;



public record ChangePasswordCommand(String accessToken, String oldPassword, String newPassword) { }
