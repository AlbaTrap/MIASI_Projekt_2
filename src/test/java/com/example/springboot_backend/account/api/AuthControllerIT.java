package com.example.springboot_backend.account.api;
 
import com.example.springboot_backend.account.application.command.ConfirmEmailCommand;
import com.example.springboot_backend.account.application.command.LoginCommand;
import com.example.springboot_backend.account.application.command.LogoutCommand;
import com.example.springboot_backend.account.application.command.RefreshSessionCommand;
import com.example.springboot_backend.account.application.command.RegisterUserCommand;
import com.example.springboot_backend.account.application.dto.LoginResponse;
import com.example.springboot_backend.account.application.dto.RegisterResponse;
import com.example.springboot_backend.account.application.service.AuthenticationApplicationService;
import com.example.springboot_backend.account.application.service.RegistrationApplicationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
class AuthControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RegistrationApplicationService registrationService;

    @MockitoBean
    private AuthenticationApplicationService authenticationService;

    @Test
    void register() throws Exception {
        // Given
        UUID accountId = UUID.randomUUID();
        RegisterResponse response = new RegisterResponse(accountId, "test@example.com", "token123");
        when(registrationService.register(any(RegisterUserCommand.class))).thenReturn(response);

        String requestJson = """
                {
                    "email": "test@example.com",
                    "password": "Password123!"
                }
                """;

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Konto utworzone. Potwierdź adres e-mail tokenem z odpowiedzi/logów."))
                .andExpect(jsonPath("$.data.email").value("test@example.com"));
    }

    @Test
    void confirm() throws Exception {
        // Given
        String token = "valid-token";
        doNothing().when(registrationService).confirmEmail(any(ConfirmEmailCommand.class));

        // When & Then
        mockMvc.perform(get("/api/auth/confirm")
                        .param("token", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Konto zostało aktywowane"));
    }

    @Test
    void login() throws Exception {
        // Given
        UUID accountId = UUID.randomUUID();
        LoginResponse response = new LoginResponse(accountId, "jwt-token", Instant.now().plusSeconds(3600));
        when(authenticationService.login(any(LoginCommand.class))).thenReturn(response);

        String requestJson = """
                {
                    "email": "test@example.com",
                    "password": "Password123!"
                }
                """;

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").value("jwt-token"));
    }

    @Test
    void logout() throws Exception {
        // Given
        String token = "valid-token";
        doNothing().when(authenticationService).logout(any(LogoutCommand.class));

        // When & Then
        mockMvc.perform(post("/api/auth/logout")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Wylogowano"));
    }

    @Test
    void refresh() throws Exception {
        // Given
        UUID accountId = UUID.randomUUID();
        LoginResponse response = new LoginResponse(accountId, "new-jwt-token", Instant.now().plusSeconds(3600));
        when(authenticationService.refresh(any(RefreshSessionCommand.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/api/auth/refresh")
                        .header("Authorization", "Bearer old-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").value("new-jwt-token"));
    }
}