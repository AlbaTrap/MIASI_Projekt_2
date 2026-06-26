package com.example.springboot_backend.system.api;

import com.example.springboot_backend.search.api.SearchController;
import com.example.springboot_backend.search.application.dto.FilterOptionsDto;
import com.example.springboot_backend.search.application.dto.SearchResultDto;
import com.example.springboot_backend.search.application.dto.SearchResultsListDto;
import com.example.springboot_backend.search.application.query.SearchEventsQuery;
import com.example.springboot_backend.search.application.service.GetFiltersService;
import com.example.springboot_backend.search.application.service.SearchEventsService;
import com.example.springboot_backend.search.application.service.UpdateSearchIndexService;
import com.example.springboot_backend.shared.security.AdminAuthorizationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SearchController.class)
public class SearchSystemTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SearchEventsService searchService;

    @MockitoBean
    private GetFiltersService filtersService;

    @MockitoBean
    private UpdateSearchIndexService indexService;

    @MockitoBean
    private AdminAuthorizationService adminAuthorizationService;

    @Test
    void shouldSearchEventsSuccessfully() throws Exception {
        // Given
        UUID eventId = UUID.randomUUID();
        SearchResultDto result = new SearchResultDto(
                eventId,
                "Found Event",
                "Description",
                Instant.now().plusSeconds(3600),
                "Wroclaw",
                "KULTURA"
        );
        SearchResultsListDto response = new SearchResultsListDto(List.of(result), 1, 0, 20);

        when(searchService.search(any(SearchEventsQuery.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(get("/api/events")
                        .param("phrase", "Event")
                        .param("category", "KULTURA")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.results[0].title").value("Found Event"))
                .andExpect(jsonPath("$.data.totalResults").value(1));
    }

    @Test
    void shouldReturnFilterOptionsSuccessfully() throws Exception {
        // Given
        FilterOptionsDto filters = new FilterOptionsDto(
                List.of("KULTURA", "SPORT"),
                List.of("Wroclaw", "Warszawa")
        );
        when(filtersService.getFilters()).thenReturn(filters);

        // When & Then
        mockMvc.perform(get("/api/events/filters")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.categories").isArray())
                .andExpect(jsonPath("$.data.categories[0]").value("KULTURA"))
                .andExpect(jsonPath("$.data.locations[0]").value("Wroclaw"));
    }

    @Test
    void shouldRebuildIndexSuccessfully() throws Exception {
        // Given
        String token = "admin-token";
        doNothing().when(adminAuthorizationService).check(token);
        when(indexService.rebuildIndex()).thenReturn(5);

        // When & Then
        mockMvc.perform(post("/api/search-index/rebuild")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Indeks wyszukiwania przebudowany"))
                .andExpect(jsonPath("$.data").value(5));
    }
}
