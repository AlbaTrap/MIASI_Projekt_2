package com.example.springboot_backend.account.api;

import com.example.springboot_backend.account.api.dto.LoginRequest;
import com.example.springboot_backend.account.api.dto.RegisterRequest;
import com.example.springboot_backend.account.application.command.*;
import com.example.springboot_backend.account.application.dto.LoginResponse;
import com.example.springboot_backend.account.application.dto.RegisterResponse;
import com.example.springboot_backend.account.application.service.AuthenticationApplicationService;
import com.example.springboot_backend.account.application.service.RegistrationApplicationService;
import com.example.springboot_backend.shared.response.ApiResponse;
import com.example.springboot_backend.shared.util.AuthHeader;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final RegistrationApplicationService registrationService;
    private final AuthenticationApplicationService authenticationService;

    public AuthController(RegistrationApplicationService registrationService, AuthenticationApplicationService authenticationService) {
        this.registrationService = registrationService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/register")
    public ApiResponse<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ApiResponse.ok("Konto utworzone. Potwierdź adres e-mail tokenem z odpowiedzi/logów.",
                registrationService.register(new RegisterUserCommand(request.email(), request.password())));
    }

    @GetMapping("/confirm")
    public ApiResponse<Void> confirm(@RequestParam String token) {
        registrationService.confirmEmail(new ConfirmEmailCommand(token));
        return ApiResponse.ok("Konto zostało aktywowane");
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.ok(authenticationService.login(new LoginCommand(request.email(), request.password())));
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(@RequestHeader(value = "Authorization", required = false) String authorization) {
        authenticationService.logout(new LogoutCommand(AuthHeader.extractToken(authorization)));
        return ApiResponse.ok("Wylogowano");
    }

    @PostMapping("/refresh")
    public ApiResponse<LoginResponse> refresh(@RequestHeader(value = "Authorization", required = false) String authorization) {
        return ApiResponse.ok(authenticationService.refresh(new RefreshSessionCommand(AuthHeader.extractToken(authorization))));
    }
}
