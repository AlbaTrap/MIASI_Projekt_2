package com.example.springboot_backend.importevents.domain.model;

import org.junit.jupiter.api.Test;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class RawEventTest {

    @Test
    void fetched() {
        // given
        ImportSource source = ImportSource.SCRAPER; // Zakładam, że ImportSource to enum lub obiekt posiadający taką wartość
        String title = "Surowy Tytuł";
        String description = "Surowy Opis Wydarzenia";
        String location = "Wrocław, Rynek";
        String date = "2026-10-12 18:00";
        String category = "Muzyka";
        Instant beforeFetch = Instant.now();

        // when
        RawEvent result = RawEvent.fetched(source, title, description, location, date, category);

        // then
        assertThat(result).isNotNull();
        assertThat(result.id()).isNotNull();
        assertThat(result.source()).isEqualTo(source);
        assertThat(result.rawTitle()).isEqualTo(title);
        assertThat(result.rawDescription()).isEqualTo(description);
        assertThat(result.rawLocation()).isEqualTo(location);
        assertThat(result.rawDate()).isEqualTo(date);
        assertThat(result.rawCategory()).isEqualTo(category);
        assertThat(result.processed()).isFalse(); // Domyślnie nowo pobrane wydarzenie nie powinno być przetworzone

        // Weryfikacja poprawności przypisania czasu pobrania
        assertThat(result.fetchedAt()).isAfterOrEqualTo(beforeFetch);
        assertThat(result.fetchedAt()).isBeforeOrEqualTo(Instant.now());
    }

    @Test
    void markAsProcessed() {
        // given
        RawEvent event = RawEvent.fetched(
                ImportSource.SCRAPER, "Tytuł", "Opis", "Lokalizacja", "Data", "Kategoria"
        );
        assertThat(event.processed()).isFalse(); // Upewniamy się, że stan początkowy to false

        // when
        event.markAsProcessed();

        // then
        assertThat(event.processed()).isTrue(); // Weryfikacja zmiany stanu flagi
    }
}
