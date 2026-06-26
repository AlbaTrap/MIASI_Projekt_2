package com.example.springboot_backend.system.api;

import com.example.springboot_backend.favorite.api.FavoriteController;
import com.example.springboot_backend.favorite.application.command.AddEventToFavoritesCommand;
import com.example.springboot_backend.favorite.application.dto.FavoriteEventDto;
import com.example.springboot_backend.favorite.application.service.FavoriteEventApplicationService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FavoriteController.class)
public class FavoriteSystemTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FavoriteEventApplicationService favoriteService;

    @Test
    void shouldAddEventToFavoritesSuccessfully() throws Exception {
        // Given
        UUID eventId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID favoriteId = UUID.randomUUID();
        String token = "valid-jwt-token";
        
        FavoriteEventDto responseDto = new FavoriteEventDto(
                favoriteId,
                userId,
                eventId,
                Instant.now()
        );

        when(favoriteService.add(any(AddEventToFavoritesCommand.class))).thenReturn(responseDto);

        // When & Then
        mockMvc.perform(post("/api/favorites/" + eventId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(favoriteId.toString()))
                .andExpect(jsonPath("$.data.eventId").value(eventId.toString()))
                .andExpect(jsonPath("$.data.userId").value(userId.toString()));
    }

    @Test
    void shouldRemoveEventFromFavoritesSuccessfully() throws Exception {
        // Given
        UUID eventId = UUID.randomUUID();
        String token = "valid-jwt-token";

        // When & Then
        mockMvc.perform(delete("/api/favorites/" + eventId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Usunięto z ulubionych"));
    }
}
