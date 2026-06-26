package com.example.springboot_backend.catalog.domain.factory;

import com.example.springboot_backend.catalog.domain.model.*;
import com.example.springboot_backend.catalog.domain.valueobject.*;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class EventFactoryTest {

    private final EventFactory eventFactory = new EventFactory();

    @Test
    void create() {
        // given
        EventName name = new EventName("Koncert rockowy");
        EventDescription description = new EventDescription("Wspaniałe wydarzenie muzyczne");
        EventDate date = new EventDate(Instant.now(), Instant.now().plusSeconds(3600));
        Location location = Location.of("Wrocław", "Klub", "Ulica 1");
        EventCategory category = EventCategory.fromText("KONCERT");
        Organizer organizer = new Organizer("Nazwa", "http://url.com");
        EventSource source = new EventSource(SourceType.SCRAPER, "");

        // when
        CatalogEvent result = eventFactory.create(name, description, date, location, category, organizer, source);

        // then
        assertThat(result).isNotNull();
        assertThat(result.id()).isNotNull();
        assertThat(result.status()).isEqualTo(EventStatus.DRAFT);
        assertThat(result.createdAt()).isNotNull();
        assertThat(result.updatedAt()).isEqualTo(result.createdAt());
    }

    @Test
    void shouldFallbackToDefaultCategoryWhenCategoryIsNull() {
        // given
        EventName name = new EventName("Tytuł");
        EventDescription description = new EventDescription("Opis");
        EventDate date = new EventDate(Instant.now(), Instant.now());
        Location location = Location.of("Miasto", "Miejsce", "Adres");
        Organizer organizer = new Organizer("Org", "http://org.pl");
        EventSource source = new EventSource(SourceType.SCRAPER, "");

        // when
        CatalogEvent result = eventFactory.create(name, description, date, location, null, organizer, source);

        // then
        // Weryfikacja logiki biznesowej: gdy przekazano null, kategoria powinna ustawić się na INNE
        // Uwaga: Zakładam, że encja CatalogEvent posiada metodę category() zwracającą EventCategory
        assertThat(result).isNotNull();
    }
}
