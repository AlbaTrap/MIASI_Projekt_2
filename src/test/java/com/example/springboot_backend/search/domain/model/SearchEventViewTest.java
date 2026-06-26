package com.example.springboot_backend.search.domain.model;

import org.junit.jupiter.api.Test;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class SearchEventViewTest {

    @Test
    void testSearchEventViewCreationAndGetters() {
        // given
        UUID eventId = UUID.randomUUID();
        String title = "Koncert muzyki filmowej";
        String shortDescription = "Wspaniały pokaz muzyki na żywo we Wrocławiu...";
        Instant startDate = Instant.parse("2026-08-20T19:00:00Z");
        String city = "Wrocław";
        String location = "Hala Stulecia";
        String category = "MUZYKA";
        String status = "PUBLISHED";

        // when
        SearchEventView view = new SearchEventView(eventId, title, shortDescription, startDate, city, location, category, status);

        // then
        assertThat(view).isNotNull();
        assertThat(view.eventId()).isEqualTo(eventId);
        assertThat(view.title()).isEqualTo(title);
        assertThat(view.shortDescription()).isEqualTo(shortDescription);
        assertThat(view.startDate()).isEqualTo(startDate);
        assertThat(view.city()).isEqualTo(city);
        assertThat(view.location()).isEqualTo(location);
        assertThat(view.category()).isEqualTo(category);
        assertThat(view.status()).isEqualTo(status);
    }
}
