package com.example.springboot_backend.importevents.api;

import com.example.springboot_backend.importevents.application.service.FetchEventsService;
import com.example.springboot_backend.importevents.application.service.NormalizeImportedEventsService;
import com.example.springboot_backend.shared.security.AdminAuthorizationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ImportEventsController.class)
class ImportEventsControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FetchEventsService fetchService;

    @MockitoBean
    private NormalizeImportedEventsService normalizeService;

    @MockitoBean
    private AdminAuthorizationService adminAuthorizationService;

    @Test
    void fetch() throws Exception {
        doNothing().when(adminAuthorizationService).check(anyString());
        when(fetchService.fetchFromScraper()).thenReturn(10);

        mockMvc.perform(post("/api/admin/import/events/fetch")
                        .header("Authorization", "Bearer admin-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Pobrano surowe wydarzenia"))
                .andExpect(jsonPath("$.data").value(10));
    }

    @Test
    void normalize() throws Exception {
        doNothing().when(adminAuthorizationService).check(anyString());
        when(normalizeService.normalize()).thenReturn("OK");

        mockMvc.perform(post("/api/admin/import/events/normalize")
                        .header("Authorization", "Bearer admin-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Normalizacja zakończona"))
                .andExpect(jsonPath("$.data").value("OK"));
    }
}