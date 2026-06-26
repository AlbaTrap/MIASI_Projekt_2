package com.example.springboot_backend.importevents.domain.service;

import com.example.springboot_backend.catalog.application.command.AddImportedEventCommand;
import com.example.springboot_backend.importevents.domain.model.ImportSource;
import com.example.springboot_backend.importevents.domain.model.RawEvent;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RawEventNormalizerTest {

    private final RawEventNormalizer normalizer = new RawEventNormalizer();

    @Test
    void normalize() {
        // =========================================================================
        // SCENARIUSZ 1: Kompletne dane wejściowe (wzorcowe rozbicie na 3 części i poprawna data)
        // =========================================================================
        RawEvent fullRawEvent = mock(RawEvent.class);
        when(fullRawEvent.rawTitle()).thenReturn("Mecz siatkówki");
        when(fullRawEvent.rawDescription()).thenReturn("Opis meczu");
        when(fullRawEvent.rawLocation()).thenReturn("Wrocław, Hala Stulecia, Wystawowa 1");
        when(fullRawEvent.rawDate()).thenReturn("2026-10-12T18:00:00Z");
        when(fullRawEvent.rawCategory()).thenReturn("SPORT");

        ImportSource mockSource = mock(ImportSource.class);
        when(mockSource.name()).thenReturn("SCRAPER");
        when(fullRawEvent.source()).thenReturn(mockSource);

        AddImportedEventCommand result1 = normalizer.normalize(fullRawEvent);

        assertThat(result1).isNotNull();
        assertThat(result1.title()).isEqualTo("Mecz siatkówki");
        assertThat(result1.description()).isEqualTo("Opis meczu");
        assertThat(result1.city()).isEqualTo("Wrocław");
        assertThat(result1.placeName()).isEqualTo("Hala Stulecia");
        assertThat(result1.address()).isEqualTo("Wystawowa 1");
        assertThat(result1.startDate()).isEqualTo(Instant.parse("2026-10-12T18:00:00Z"));
        assertThat(result1.organizerName()).isEqualTo("Wrocławski Klub Sportowy"); // z instrukcji switch
        assertThat(result1.source()).isEqualTo("SCRAPER");

        // =========================================================================
        // SCENARIUSZ 2: Błędna data oraz domyślny organizator (default w switch)
        // =========================================================================
        RawEvent faultyDateEvent = mock(RawEvent.class);
        when(faultyDateEvent.rawTitle()).thenReturn("Nietypowy Tytuł Wydarzenia");
        when(faultyDateEvent.rawLocation()).thenReturn("Wrocław, Rynek, Sukiennice 1");
        when(faultyDateEvent.rawDate()).thenReturn("niepoprawny-format-daty");
        when(faultyDateEvent.source()).thenReturn(mockSource);

        Instant beforeNormalization = Instant.now();
        AddImportedEventCommand result2 = normalizer.normalize(faultyDateEvent);

        // Powinno podstawić datę jutrzejszą (Instant.now().plusSeconds(86400))
        assertThat(result2.startDate()).isAfterOrEqualTo(beforeNormalization.plusSeconds(86400));
        assertThat(result2.organizerName()).isEqualTo("Organizator wydarzenia"); // default z instrukcji switch

        // =========================================================================
        // SCENARIUSZ 3: Lokalizacja pusta / null (splitLocation - pierwsza ścieżka)
        // =========================================================================
        RawEvent emptyLocEvent = mock(RawEvent.class);
        when(emptyLocEvent.rawTitle()).thenReturn("Spektakl komediowy");
        when(emptyLocEvent.rawLocation()).thenReturn("   ");
        when(emptyLocEvent.rawDate()).thenReturn("2026-10-12T18:00:00Z");
        when(emptyLocEvent.source()).thenReturn(mockSource);

        AddImportedEventCommand result3 = normalizer.normalize(emptyLocEvent);
        assertThat(result3.city()).isEqualTo("Wrocław");
        assertThat(result3.placeName()).isEqualTo("Brak nazwy miejsca");
        assertThat(result3.address()).isEqualTo("Brak adresu");

        // =========================================================================
        // SCENARIUSZ 4: Lokalizacja z 1 częścią (splitLocation - druga ścieżka)
        // =========================================================================
        RawEvent singleLocEvent = mock(RawEvent.class);
        when(singleLocEvent.rawTitle()).thenReturn("Spektakl komediowy");
        when(singleLocEvent.rawLocation()).thenReturn("Klub Muzyczny");
        when(singleLocEvent.rawDate()).thenReturn("2026-10-12T18:00:00Z");
        when(singleLocEvent.source()).thenReturn(mockSource);

        AddImportedEventCommand result4 = normalizer.normalize(singleLocEvent);
        assertThat(result4.city()).isEqualTo("Wrocław");
        assertThat(result4.placeName()).isEqualTo("Klub Muzyczny");
        assertThat(result4.address()).isEqualTo("Klub Muzyczny");

        // =========================================================================
        // SCENARIUSZ 5: Lokalizacja z 2 częściami (splitLocation - trzecia ścieżka)
        // =========================================================================
        RawEvent doubleLocEvent = mock(RawEvent.class);
        when(doubleLocEvent.rawTitle()).thenReturn("Spektakl komediowy");
        when(doubleLocEvent.rawLocation()).thenReturn("Gdańsk, Teatr Wybrzeże");
        when(doubleLocEvent.rawDate()).thenReturn("2026-10-12T18:00:00Z");
        when(doubleLocEvent.source()).thenReturn(mockSource);

        AddImportedEventCommand result5 = normalizer.normalize(doubleLocEvent);
        assertThat(result5.city()).isEqualTo("Gdańsk");
        assertThat(result5.placeName()).isEqualTo("Teatr Wybrzeże");
        assertThat(result5.address()).isEqualTo("Teatr Wybrzeże");
    }
}
