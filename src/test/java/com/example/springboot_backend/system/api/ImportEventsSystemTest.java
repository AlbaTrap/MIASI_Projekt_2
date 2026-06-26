package com.example.springboot_backend.system.api;

import com.example.springboot_backend.importevents.api.ImportEventsController;
import com.example.springboot_backend.importevents.application.service.FetchEventsService;
import com.example.springboot_backend.importevents.application.service.NormalizeImportedEventsService;
import com.example.springboot_backend.shared.security.AdminAuthorizationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ImportEventsController.class)
public class ImportEventsSystemTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FetchEventsService fetchService;

    @MockitoBean
    private NormalizeImportedEventsService normalizeService;

    @MockitoBean
    private AdminAuthorizationService adminAuthorizationService;

    @Test
    void shouldFetchEventsSuccessfully() throws Exception {
        // Given
        String token = "admin-token";
        int fetchedCount = 10;
        
        doNothing().when(adminAuthorizationService).check(token);
        when(fetchService.fetchFromScraper()).thenReturn(fetchedCount);

        // When & Then
        mockMvc.perform(post("/api/admin/import/events/fetch")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Pobrano surowe wydarzenia"))
                .andExpect(jsonPath("$.data").value(fetchedCount));
    }

    @Test
    void shouldNormalizeEventsSuccessfully() throws Exception {
        // Given
        String token = "admin-token";
        String resultMessage = "Normalized 5 events";

        doNothing().when(adminAuthorizationService).check(token);
        when(normalizeService.normalize()).thenReturn(resultMessage);

        // When & Then
        mockMvc.perform(post("/api/admin/import/events/normalize")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Normalizacja zakończona"))
                .andExpect(jsonPath("$.data").value(resultMessage));
    }
}
