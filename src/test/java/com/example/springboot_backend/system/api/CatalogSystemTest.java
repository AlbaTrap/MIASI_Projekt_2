package com.example.springboot_backend.system.api;

import com.example.springboot_backend.catalog.api.CatalogController;
import com.example.springboot_backend.catalog.application.dto.CatalogEventDto;
import com.example.springboot_backend.catalog.application.service.EventQueryService;
import com.example.springboot_backend.catalog.domain.model.EventCategory;
import com.example.springboot_backend.catalog.domain.model.EventStatus;
import com.example.springboot_backend.catalog.domain.valueobject.SourceType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CatalogController.class)
public class CatalogSystemTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EventQueryService eventQueryService;

    @Test
    void shouldReturnListOfPublishedEvents() throws Exception {
        // Given
        UUID eventId = UUID.randomUUID();
        CatalogEventDto event = new CatalogEventDto(
                eventId,
                "Test Event",
                "Description",
                "Place",
                "Wroclaw",
                "Street 1",
                Instant.now().plusSeconds(3600),
                Instant.now().plusSeconds(7200),
                EventCategory.INNE,
                "Organizer",
                SourceType.ADMINISTRATOR,
                EventStatus.PUBLISHED,
                Instant.now(),
                Instant.now(),
                null
        );

        when(eventQueryService.getPublishedEvents()).thenReturn(List.of(event));

        // When & Then
        mockMvc.perform(get("/api/catalog/events")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value(eventId.toString()))
                .andExpect(jsonPath("$.data[0].title").value("Test Event"))
                .andExpect(jsonPath("$.data[0].status").value("PUBLISHED"));
    }

    @Test
    void shouldReturnEventDetailsSuccessfully() throws Exception {
        // Given
        UUID eventId = UUID.randomUUID();
        CatalogEventDto event = new CatalogEventDto(
                eventId,
                "Detailed Event",
                "Full Description",
                "Main Square",
                "Wroclaw",
                "Rynek 1",
                Instant.now().plusSeconds(3600),
                Instant.now().plusSeconds(7200),
                EventCategory.KULTURA,
                "City Hall",
                SourceType.ADMINISTRATOR,
                EventStatus.PUBLISHED,
                Instant.now(),
                Instant.now(),
                null
        );

        when(eventQueryService.getPublicEvent(eventId)).thenReturn(event);

        // When & Then
        mockMvc.perform(get("/api/catalog/events/" + eventId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(eventId.toString()))
                .andExpect(jsonPath("$.data.title").value("Detailed Event"))
                .andExpect(jsonPath("$.data.category").value("KULTURA"));
    }
}
