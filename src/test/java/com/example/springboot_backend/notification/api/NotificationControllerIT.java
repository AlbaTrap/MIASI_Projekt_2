package com.example.springboot_backend.notification.api;

import com.example.springboot_backend.notification.application.dto.InformatorDto;
import com.example.springboot_backend.notification.application.dto.NotificationDto;
import com.example.springboot_backend.notification.application.service.CreateInformatorApplicationService;
import com.example.springboot_backend.notification.application.service.NotificationQueryApplicationService;
import com.example.springboot_backend.notification.application.service.SendNotificationApplicationService;
import com.example.springboot_backend.notification.domain.model.NotificationStatus;
import com.example.springboot_backend.notification.domain.valueobject.NotificationChannel;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NotificationController.class)
class NotificationControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SendNotificationApplicationService sendService;

    @MockitoBean
    private NotificationQueryApplicationService queryService;

    @MockitoBean
    private CreateInformatorApplicationService informatorService;

    @Test
    void send() throws Exception {
        UUID eventId = UUID.randomUUID();
        NotificationDto dto = createNotificationDto(eventId);
        when(sendService.send(any())).thenReturn(dto);

        mockMvc.perform(post("/api/notifications/events/" + eventId)
                        .header("Authorization", "Bearer token")
                        .param("channel", "EMAIL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.eventId").value(eventId.toString()))
                .andExpect(jsonPath("$.data.channel").value("EMAIL"));
    }

    @Test
    void mine() throws Exception {
        NotificationDto dto = createNotificationDto(UUID.randomUUID());
        when(queryService.mine(any())).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/notifications")
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value(dto.id().toString()));
    }

    @Test
    void createInformator() throws Exception {
        UUID eventId = UUID.randomUUID();
        UUID notificationId = UUID.randomUUID();
        InformatorDto dto = createInformatorDto(eventId, notificationId);
        when(informatorService.create(any(), any(), any())).thenReturn(dto);

        mockMvc.perform(post("/api/informators/events/" + eventId + "/notifications/" + notificationId)
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.eventId").value(eventId.toString()))
                .andExpect(jsonPath("$.data.notificationId").value(notificationId.toString()));
    }

    @Test
    void informators() throws Exception {
        InformatorDto dto = createInformatorDto(UUID.randomUUID(), UUID.randomUUID());
        when(informatorService.mine(any())).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/informators")
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value(dto.id().toString()));
    }

    private NotificationDto createNotificationDto(UUID eventId) {
        return new NotificationDto(
                UUID.randomUUID(),
                UUID.randomUUID(),
                eventId,
                NotificationChannel.EMAIL,
                NotificationStatus.SENT,
                "Subject",
                "Message",
                Instant.now(),
                Instant.now(),
                null
        );
    }

    private InformatorDto createInformatorDto(UUID eventId, UUID notificationId) {
        return new InformatorDto(
                UUID.randomUUID(),
                UUID.randomUUID(),
                eventId,
                notificationId,
                "Informator Message",
                Instant.now()
        );
    }
}