package com.example.springboot_backend.account.api;

import com.example.springboot_backend.account.api.dto.BlockAccountRequest;
import com.example.springboot_backend.account.api.dto.ChangePasswordRequest;
import com.example.springboot_backend.account.api.dto.ChangePhoneNumberRequest;
import com.example.springboot_backend.account.application.command.BlockAccountCommand;
import com.example.springboot_backend.account.application.command.ChangePasswordCommand;
import com.example.springboot_backend.account.application.command.ChangePhoneNumberCommand;
import com.example.springboot_backend.account.application.command.DeleteAccountCommand;
import com.example.springboot_backend.account.application.dto.UserDto;
import com.example.springboot_backend.account.application.service.AccountManagementApplicationService;
import com.example.springboot_backend.account.application.service.AccountQueryApplicationService;
import com.example.springboot_backend.shared.security.AdminAuthorizationService;
import com.example.springboot_backend.account.domain.model.AccountStatus;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountController.class)
class AccountControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AccountQueryApplicationService queryService;

    @MockitoBean
    private AccountManagementApplicationService managementService;

    @MockitoBean
    private AdminAuthorizationService adminAuthorizationService;

    @Test
    void me() throws Exception {
        // Given
        String token = "valid-token";
        UserDto userDto = new UserDto(UUID.randomUUID(), "test@example.com", AccountStatus.ACTIVE, Instant.now(), Instant.now(), "123456789");
        when(queryService.me(token)).thenReturn(userDto);

        // When & Then
        mockMvc.perform(get("/api/account/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.email").value("test@example.com"));
    }

    @Test
    void changePassword() throws Exception {
        // Given
        String token = "valid-token";
        String requestJson = """
                {
                    "oldPassword": "oldPassword123",
                    "newPassword": "newPassword123"
                }
                """;
        doNothing().when(managementService).changePassword(any(ChangePasswordCommand.class));

        // When & Then
        mockMvc.perform(put("/api/account/password")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Hasło zostało zmienione, aktywne sesje unieważniono"));
    }

    @Test
    void deleteMe() throws Exception {
        // Given
        String token = "valid-token";
        doNothing().when(managementService).delete(any(DeleteAccountCommand.class));

        // When & Then
        mockMvc.perform(delete("/api/account/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Konto zostało oznaczone jako usunięte"));
    }

    @Test
    void changePhoneNumber() throws Exception {
        // Given
        String token = "valid-token";
        String requestJson = """
                {
                    "phoneNumber": "987654321"
                }
                """;
        doNothing().when(managementService).changePhoneNumber(any(ChangePhoneNumberCommand.class));

        // When & Then
        mockMvc.perform(put("/api/account/phone-number")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Numer telefonu został zapisany"));
    }

    @Test
    void block() throws Exception {
        // Given
        UUID accountId = UUID.randomUUID();
        String token = "admin-token";
        String requestJson = """
                {
                    "reason": "Violation of terms"
                }
                """;
        doNothing().when(adminAuthorizationService).check(token);
        doNothing().when(managementService).block(any(BlockAccountCommand.class));

        // When & Then
        mockMvc.perform(patch("/api/account/admin/" + accountId + "/block")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Konto zostało zablokowane"));
    }
}