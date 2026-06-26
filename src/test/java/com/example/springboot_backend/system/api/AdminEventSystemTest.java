package com.example.springboot_backend.system.api;

import com.example.springboot_backend.catalog.api.AdminEventController;
import com.example.springboot_backend.catalog.application.command.AddEventCommand;
import com.example.springboot_backend.catalog.application.dto.CatalogEventDto;
import com.example.springboot_backend.catalog.application.service.EventAdditionService;
import com.example.springboot_backend.catalog.application.service.EventPublicationService;
import com.example.springboot_backend.catalog.application.service.EventUpdateService;
import com.example.springboot_backend.catalog.domain.model.EventCategory;
import com.example.springboot_backend.catalog.domain.model.EventStatus;
import com.example.springboot_backend.catalog.domain.valueobject.SourceType;
import com.example.springboot_backend.shared.security.AdminAuthorizationService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminEventController.class)
public class AdminEventSystemTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EventAdditionService additionService;

    @MockitoBean
    private EventUpdateService updateService;

    @MockitoBean
    private EventPublicationService publicationService;

    @MockitoBean
    private AdminAuthorizationService adminAuthorizationService;

    @Test
    void shouldAddEventSuccessfully() throws Exception {
        // Given
        String token = "admin-token";
        UUID eventId = UUID.randomUUID();
        CatalogEventDto responseDto = new CatalogEventDto(
                eventId,
                "New Event",
                "Description",
                "Place",
                "Wroclaw",
                "Street 1",
                Instant.parse("2026-07-01T10:00:00Z"),
                Instant.parse("2026-07-01T12:00:00Z"),
                EventCategory.KULTURA,
                "Organizer",
                SourceType.ADMINISTRATOR,
                EventStatus.DRAFT,
                Instant.now(),
                Instant.now(),
                null
        );

        doNothing().when(adminAuthorizationService).check(token);
        when(additionService.addEvent(any(AddEventCommand.class))).thenReturn(responseDto);

        String requestJson = """
                {
                    "title": "New Event",
                    "description": "Description",
                    "placeName": "Place",
                    "city": "Wroclaw",
                    "address": "Street 1",
                    "startDate": "2026-07-01T10:00:00Z",
                    "endDate": "2026-07-01T12:00:00Z",
                    "category": "KULTURA",
                    "organizerName": "Organizer",
                    "organizerWebsite": "http://example.com"
                }
                """;

        // When & Then
        mockMvc.perform(post("/api/admin/events")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Wydarzenie dodane jako szkic"))
                .andExpect(jsonPath("$.data.id").value(eventId.toString()))
                .andExpect(jsonPath("$.data.title").value("New Event"))
                .andExpect(jsonPath("$.data.status").value("DRAFT"));
    }

    @Test
    void shouldReturnBadRequestWhenTitleIsMissing() throws Exception {
        // Given
        String token = "admin-token";
        String requestJson = """
                {
                    "title": "",
                    "city": "Wroclaw",
                    "address": "Street 1",
                    "startDate": "2026-07-01T10:00:00Z"
                }
                """;

        // When & Then
        mockMvc.perform(post("/api/admin/events")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());
    }
}
