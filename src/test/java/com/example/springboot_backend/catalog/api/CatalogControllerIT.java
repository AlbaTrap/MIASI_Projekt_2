package com.example.springboot_backend.catalog.api;

import com.example.springboot_backend.catalog.application.dto.CatalogEventDto;
import com.example.springboot_backend.catalog.application.service.EventQueryService;
import com.example.springboot_backend.catalog.domain.model.EventCategory;
import com.example.springboot_backend.catalog.domain.model.EventStatus;
import com.example.springboot_backend.catalog.domain.valueobject.SourceType;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CatalogController.class)
class CatalogControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EventQueryService queryService;

    @Test
    void published() throws Exception {
        // given
        CatalogEventDto event = createEventDto(UUID.randomUUID(), "Published Event", EventStatus.PUBLISHED);
        when(queryService.getPublishedEvents()).thenReturn(List.of(event));

        // when & then
        mockMvc.perform(get("/api/catalog/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].title").value("Published Event"))
                .andExpect(jsonPath("$.data[0].status").value("PUBLISHED"));
    }

    @Test
    void details() throws Exception {
        // given
        UUID id = UUID.randomUUID();
        CatalogEventDto event = createEventDto(id, "Event Details", EventStatus.PUBLISHED);
        when(queryService.getPublicEvent(id)).thenReturn(event);

        // when & then
        mockMvc.perform(get("/api/catalog/events/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(id.toString()))
                .andExpect(jsonPath("$.data.title").value("Event Details"));
    }

    private CatalogEventDto createEventDto(UUID id, String title, EventStatus status) {
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
                status,
                Instant.now(),
                Instant.now(),
                null
        );
    }
}