package com.example.springboot_backend.system.api;

import com.example.springboot_backend.account.api.AuthController;
import com.example.springboot_backend.account.application.command.LoginCommand;
import com.example.springboot_backend.account.application.command.RegisterUserCommand;
import com.example.springboot_backend.account.application.dto.LoginResponse;
import com.example.springboot_backend.account.application.dto.RegisterResponse;
import com.example.springboot_backend.account.application.service.AuthenticationApplicationService;
import com.example.springboot_backend.account.application.service.RegistrationApplicationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
public class AuthSystemTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RegistrationApplicationService registrationService;

    @MockitoBean
    private AuthenticationApplicationService authenticationService;

    @Nested
    @DisplayName("Rejestracja użytkownika")
    class Registration {

        @Test
        void shouldRegisterAccountSuccessfully() throws Exception {
            // Given
            String email = "test@example.com";
            String password = "password123";
            UUID accountId = UUID.randomUUID();
            String verificationToken = "token123";

            RegisterResponse response = new RegisterResponse(accountId, email, verificationToken);

            when(registrationService.register(any(RegisterUserCommand.class))).thenReturn(response);

            String requestJson = """
                    {
                        "email": "%s",
                        "password": "%s"
                    }
                    """.formatted(email, password);

            // When & Then
            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJson))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("Konto utworzone. Potwierdź adres e-mail tokenem z odpowiedzi/logów."))
                    .andExpect(jsonPath("$.data.email").value(email))
                    .andExpect(jsonPath("$.data.verificationTokenForDemo").value(verificationToken))
                    .andExpect(jsonPath("$.data.accountId").value(accountId.toString()));
        }

        @Test
        void shouldReturnBadRequestWhenEmailIsInvalid() throws Exception {
            // Given
            String invalidEmail = "invalid-email";
            String password = "password123";

            String requestJson = """
                    {
                        "email": "%s",
                        "password": "%s"
                    }
                    """.formatted(invalidEmail, password);

            // When & Then
            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJson))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("Logowanie użytkownika")
    class Login {

        @Test
        void shouldLoginSuccessfully() throws Exception {
            // Given
            String email = "user@example.com";
            String password = "Password123!";
            UUID accountId = UUID.randomUUID();
            String token = "jwt-token-example";
            Instant expiresAt = Instant.now().plusSeconds(3600);

            LoginResponse response = new LoginResponse(accountId, token, expiresAt);

            when(authenticationService.login(any(LoginCommand.class))).thenReturn(response);

            String requestJson = """
                    {
                        "email": "%s",
                        "password": "%s"
                    }
                    """.formatted(email, password);

            // When & Then
            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJson))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.accountId").value(accountId.toString()))
                    .andExpect(jsonPath("$.data.accessToken").value(token))
                    .andExpect(jsonPath("$.data.expiresAt").exists());
        }

        @Test
        void shouldReturnBadRequestWhenLoginEmailIsInvalid() throws Exception {
            // Given
            String invalidEmail = "not-an-email";
            String password = "Password123!";

            String requestJson = """
                    {
                        "email": "%s",
                        "password": "%s"
                    }
                    """.formatted(invalidEmail, password);

            // When & Then
            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJson))
                    .andExpect(status().isBadRequest());
        }
    }
}
