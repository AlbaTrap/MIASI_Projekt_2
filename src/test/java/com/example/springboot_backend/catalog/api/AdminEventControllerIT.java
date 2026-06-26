package com.example.springboot_backend.catalog.api;

import com.example.springboot_backend.catalog.api.dto.CancelEventRequest;
import com.example.springboot_backend.catalog.api.dto.EventRequest;
import com.example.springboot_backend.catalog.application.command.AddEventCommand;
import com.example.springboot_backend.catalog.application.command.CancelEventCommand;
import com.example.springboot_backend.catalog.application.command.EventIdCommand;
import com.example.springboot_backend.catalog.application.command.UpdateEventCommand;
import com.example.springboot_backend.catalog.application.dto.CatalogEventDto;
import com.example.springboot_backend.catalog.application.service.EventAdditionService;
import com.example.springboot_backend.catalog.application.service.EventPublicationService;
import com.example.springboot_backend.catalog.application.service.EventUpdateService;
import com.example.springboot_backend.catalog.domain.model.EventCategory;
import com.example.springboot_backend.catalog.domain.model.EventStatus;
import com.example.springboot_backend.catalog.domain.valueobject.SourceType;
import com.example.springboot_backend.shared.security.AdminAuthorizationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminEventController.class)
class AdminEventControllerIT {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @MockitoBean
    private EventAdditionService additionService;

    @MockitoBean
    private EventUpdateService updateService;

    @MockitoBean
    private EventPublicationService publicationService;

    @MockitoBean
    private AdminAuthorizationService adminAuthorizationService;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void add() throws Exception {
        // Given
        EventRequest request = createEventRequest();
        CatalogEventDto responseDto = createCatalogEventDto(UUID.randomUUID(), request.title());
        when(additionService.addEvent(any(AddEventCommand.class))).thenReturn(responseDto);

        // When & Then
        mockMvc.perform(post("/api/admin/events")
                        .header("Authorization", "Bearer admin-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Wydarzenie dodane jako szkic"))
                .andExpect(jsonPath("$.data.title").value(request.title()));

        verify(adminAuthorizationService).check("admin-token");
    }

    @Test
    void update() throws Exception {
        // Given
        UUID id = UUID.randomUUID();
        EventRequest request = createEventRequest();
        CatalogEventDto responseDto = createCatalogEventDto(id, request.title());
        when(updateService.updateEvent(any(UpdateEventCommand.class))).thenReturn(responseDto);

        // When & Then
        mockMvc.perform(put("/api/admin/events/{id}", id)
                        .header("Authorization", "Bearer admin-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Wydarzenie zaktualizowane"))
                .andExpect(jsonPath("$.data.id").value(id.toString()));

        verify(adminAuthorizationService).check("admin-token");
    }

    @Test
    void publish() throws Exception {
        // Given
        UUID id = UUID.randomUUID();
        CatalogEventDto responseDto = createCatalogEventDto(id, "Published Event");
        when(publicationService.publish(any(EventIdCommand.class))).thenReturn(responseDto);

        // When & Then
        mockMvc.perform(post("/api/admin/events/{id}/publish", id)
                        .header("Authorization", "Bearer admin-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(id.toString()));

        verify(adminAuthorizationService).check("admin-token");
    }

    @Test
    void hide() throws Exception {
        // Given
        UUID id = UUID.randomUUID();
        CatalogEventDto responseDto = createCatalogEventDto(id, "Hidden Event");
        when(publicationService.hide(any(EventIdCommand.class))).thenReturn(responseDto);

        // When & Then
        mockMvc.perform(post("/api/admin/events/{id}/hide", id)
                        .header("Authorization", "Bearer admin-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(id.toString()));

        verify(adminAuthorizationService).check("admin-token");
    }

    @Test
    void archive() throws Exception {
        // Given
        UUID id = UUID.randomUUID();
        CatalogEventDto responseDto = createCatalogEventDto(id, "Archived Event");
        when(publicationService.archive(any(EventIdCommand.class))).thenReturn(responseDto);

        // When & Then
        mockMvc.perform(post("/api/admin/events/{id}/archive", id)
                        .header("Authorization", "Bearer admin-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(id.toString()));

        verify(adminAuthorizationService).check("admin-token");
    }

    @Test
    void cancel() throws Exception {
        // Given
        UUID id = UUID.randomUUID();
        CancelEventRequest request = new CancelEventRequest("Reason for cancellation");
        CatalogEventDto responseDto = createCatalogEventDto(id, "Cancelled Event");
        when(updateService.cancelEvent(any(CancelEventCommand.class))).thenReturn(responseDto);

        // When & Then
        mockMvc.perform(post("/api/admin/events/{id}/cancel", id)
                        .header("Authorization", "Bearer admin-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(id.toString()));

        verify(adminAuthorizationService).check("admin-token");
    }

    @Test
    void deleteEvent() throws Exception {
        // Given
        UUID id = UUID.randomUUID();

        // When & Then
        mockMvc.perform(delete("/api/admin/events/{id}", id)
                        .header("Authorization", "Bearer admin-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Wydarzenie usunięte"));

        verify(adminAuthorizationService).check("admin-token");
        verify(publicationService).delete(any(EventIdCommand.class));
    }

    private EventRequest createEventRequest() {
        return new EventRequest(
                "Test Event",
                "Description",
                "Place",
                "City",
                "Address",
                Instant.now().plusSeconds(3600),
                Instant.now().plusSeconds(7200),
                "KONCERT",
                "Organizer",
                "Website"
        );
    }

    private CatalogEventDto createCatalogEventDto(UUID id, String title) {
        return new CatalogEventDto(
                id,
                title,
                "Description",
                "Place",
                "City",
                "Address",
                Instant.now(),
                Instant.now().plusSeconds(3600),
                EventCategory.KONCERT,
                "Organizer",
                SourceType.ADMINISTRATOR,
                EventStatus.DRAFT,
                Instant.now(),
                Instant.now(),
                null
        );
    }
}