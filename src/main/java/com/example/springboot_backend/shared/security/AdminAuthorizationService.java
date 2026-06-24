package com.example.springboot_backend.shared.security;

import com.example.springboot_backend.shared.exception.UnauthorizedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AdminAuthorizationService {
    private final String adminToken;
    public AdminAuthorizationService(@Value("${app.admin-token:admin-token}") String adminToken) { this.adminToken = adminToken; }
    public void check(String accessToken) {
        if (accessToken == null || accessToken.isBlank() || !accessToken.equals(adminToken)) throw new UnauthorizedException("Brak uprawnień administratora");
    }
}
