package com.example.springboot_backend.search.application.service;

import com.example.springboot_backend.search.application.dto.FilterOptionsDto;
import com.example.springboot_backend.search.domain.repository.EventIndexRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetFiltersServiceTest {

    @Mock
    private EventIndexRepository repository;

    @InjectMocks
    private GetFiltersService getFiltersService;

    @Test
    void getFilters() {
        // given
        List<String> mockCategories = List.of("KONCERT", "SPORT", "TEATR");
        List<String> mockLocations = List.of("Hala Stulecia", "Rynek", "Stary Klasztor");

        when(repository.getCategories()).thenReturn(mockCategories);
        when(repository.getLocations()).thenReturn(mockLocations);

        // when
        FilterOptionsDto result = getFiltersService.getFilters();

        // then
        // Weryfikacja, czy dane zwrócone z serwisu odpowiadają danym z repozytorium
        assertThat(result).isNotNull();
        assertThat(result.categories()).containsExactlyInAnyOrder("KONCERT", "SPORT", "TEATR");
        assertThat(result.locations()).containsExactlyInAnyOrder("Hala Stulecia", "Rynek", "Stary Klasztor");

        // Weryfikacja, czy metody repozytorium zostały wywołane dokładnie raz
        verify(repository).getCategories();
        verify(repository).getLocations();
    }
}
