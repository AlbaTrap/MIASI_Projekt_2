package com.example.springboot_backend.search.api;

import com.example.springboot_backend.search.application.dto.FilterOptionsDto;
import com.example.springboot_backend.search.application.dto.SearchResultDto;
import com.example.springboot_backend.search.application.dto.SearchResultsListDto;
import com.example.springboot_backend.search.application.service.GetFiltersService;
import com.example.springboot_backend.search.application.service.SearchEventsService;
import com.example.springboot_backend.search.application.service.UpdateSearchIndexService;
import com.example.springboot_backend.shared.security.AdminAuthorizationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SearchController.class)
class SearchControllerIT {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @MockitoBean
    private SearchEventsService searchService;

    @MockitoBean
    private GetFiltersService filtersService;

    @MockitoBean
    private UpdateSearchIndexService indexService;

    @MockitoBean
    private AdminAuthorizationService adminAuthorizationService;

    @Test
    void search() throws Exception {
        UUID eventId = UUID.randomUUID();
        SearchResultDto resultDto = new SearchResultDto(
                eventId,
                "Test Event",
                "Description",
                Instant.now(),
                "Wrocław",
                "CONCERT"
        );
        SearchResultsListDto response = new SearchResultsListDto(List.of(resultDto), 1, 0, 20);

        when(searchService.search(any())).thenReturn(response);

        mockMvc.perform(get("/api/events")
                        .param("phrase", "test")
                        .param("category", "CONCERT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.results[0].title").value("Test Event"))
                .andExpect(jsonPath("$.data.totalResults").value(1));
    }

    @Test
    void filters() throws Exception {
        FilterOptionsDto filters = new FilterOptionsDto(
                List.of("CONCERT", "SPORT"),
                List.of("Wrocław", "Warszawa")
        );

        when(filtersService.getFilters()).thenReturn(filters);

        mockMvc.perform(get("/api/events/filters"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.categories[0]").value("CONCERT"))
                .andExpect(jsonPath("$.data.locations[0]").value("Wrocław"));
    }

    @Test
    void rebuildIndex() throws Exception {
        String token = "admin-token";
        when(indexService.rebuildIndex()).thenReturn(10);

        mockMvc.perform(post("/api/search-index/rebuild")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Indeks wyszukiwania przebudowany"))
                .andExpect(jsonPath("$.data").value(10));

        verify(adminAuthorizationService).check(token);
    }

    @Test
    void addToIndex() throws Exception {
        UUID id = UUID.randomUUID();
        String token = "admin-token";

        mockMvc.perform(post("/api/search-index/events/" + id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Wydarzenie dodane do indeksu"));

        verify(adminAuthorizationService).check(token);
        verify(indexService).addToIndex(id);
    }

    @Test
    void removeFromIndex() throws Exception {
        UUID id = UUID.randomUUID();
        String token = "admin-token";

        mockMvc.perform(delete("/api/search-index/events/" + id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Wydarzenie usunięte z indeksu"));

        verify(adminAuthorizationService).check(token);
        verify(indexService).removeFromIndex(id);
    }
}