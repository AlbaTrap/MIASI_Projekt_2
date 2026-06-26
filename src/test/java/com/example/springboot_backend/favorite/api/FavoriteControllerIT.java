package com.example.springboot_backend.favorite.api;
    
import com.example.springboot_backend.catalog.application.dto.CatalogEventDto;
import com.example.springboot_backend.catalog.domain.model.EventCategory;
import com.example.springboot_backend.catalog.domain.model.EventStatus;
import com.example.springboot_backend.catalog.domain.valueobject.SourceType;
import com.example.springboot_backend.favorite.application.dto.FavoriteEventDto;
import com.example.springboot_backend.favorite.application.service.FavoriteEventApplicationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FavoriteController.class)
class FavoriteControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FavoriteEventApplicationService favoriteService;

    @Test
    void add() throws Exception {
        // given
        UUID eventId = UUID.randomUUID();
        FavoriteEventDto responseDto = new FavoriteEventDto(UUID.randomUUID(), UUID.randomUUID(), eventId, Instant.now());
        when(favoriteService.add(any())).thenReturn(responseDto);

        // when & then
        mockMvc.perform(post("/api/favorites/{eventId}", eventId)
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.eventId").value(eventId.toString()));
    }

    @Test
    void remove() throws Exception {
        // given
        UUID eventId = UUID.randomUUID();
        doNothing().when(favoriteService).remove(any());

        // when & then
        mockMvc.perform(delete("/api/favorites/{eventId}", eventId)
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Usunięto z ulubionych"));
    }

    @Test
    void mine() throws Exception {
        // given
        UUID eventId = UUID.randomUUID();
        FavoriteEventDto favDto = new FavoriteEventDto(UUID.randomUUID(), UUID.randomUUID(), eventId, Instant.now());
        when(favoriteService.mine(any())).thenReturn(List.of(favDto));

        // when & then
        mockMvc.perform(get("/api/favorites")
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].eventId").value(eventId.toString()));
    }

    @Test
    void events() throws Exception {
        // given
        CatalogEventDto eventDto = createCatalogEventDto(UUID.randomUUID(), "Favorite Event");
        when(favoriteService.events(any())).thenReturn(List.of(eventDto));

        // when & then
        mockMvc.perform(get("/api/favorites/events")
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].title").value("Favorite Event"));
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
                EventStatus.PUBLISHED,
                Instant.now(),
                Instant.now(),
                null
        );
    }
}