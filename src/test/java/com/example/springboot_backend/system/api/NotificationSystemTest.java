package com.example.springboot_backend.system.api;

import com.example.springboot_backend.notification.api.NotificationController;
import com.example.springboot_backend.notification.application.dto.NotificationDto;
import com.example.springboot_backend.notification.application.service.CreateInformatorApplicationService;
import com.example.springboot_backend.notification.application.service.NotificationQueryApplicationService;
import com.example.springboot_backend.notification.application.service.SendNotificationApplicationService;
import com.example.springboot_backend.notification.domain.model.NotificationStatus;
import com.example.springboot_backend.notification.domain.valueobject.NotificationChannel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NotificationController.class)
public class NotificationSystemTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SendNotificationApplicationService sendService;

    @MockitoBean
    private NotificationQueryApplicationService queryService;

    @MockitoBean
    private CreateInformatorApplicationService informatorService;

    @Test
    void shouldReturnUserNotificationsSuccessfully() throws Exception {
        // Given
        UUID notificationId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID eventId = UUID.randomUUID();
        String token = "valid-jwt-token";

        NotificationDto notificationDto = new NotificationDto(
                notificationId,
                userId,
                eventId,
                NotificationChannel.EMAIL,
                NotificationStatus.SENT,
                "Test Subject",
                "Test Message",
                Instant.now(),
                Instant.now(),
                null
        );

        when(queryService.mine(anyString())).thenReturn(List.of(notificationDto));

        // When & Then
        mockMvc.perform(get("/api/notifications")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value(notificationId.toString()))
                .andExpect(jsonPath("$.data[0].subject").value("Test Subject"))
                .andExpect(jsonPath("$.data[0].status").value("SENT"));
    }
}
