package com.example.springboot_backend.catalog.domain.model;

import com.example.springboot_backend.catalog.domain.valueobject.*;
import com.example.springboot_backend.shared.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CatalogEventTest {

    private CatalogEventId id;
    private EventName initialName;
    private EventDescription initialDescription;
    private EventDate initialDate;
    private Location initialLocation;
    private EventCategory initialCategory;
    private Organizer initialOrganizer;
    private EventSource initialSource;
    private Instant initialTime;

    @BeforeEach
    void setUp() {
        id = CatalogEventId.of(java.util.UUID.randomUUID());
        initialName = new EventName("Koncert");
        initialDescription = new EventDescription("Opis");
        initialDate = mock(EventDate.class);
        initialLocation = mock(Location.class);
        initialCategory = EventCategory.fromText("KONCERT");
        initialOrganizer = mock(Organizer.class);
        initialSource = new EventSource(SourceType.SCRAPER, "");
        initialTime = Instant.now().minusSeconds(60); // Czas w przeszłości, aby wykryć wywołanie touch()
    }

    private CatalogEvent createDraftEvent() {
        return new CatalogEvent(id, initialName, initialDescription, initialDate, initialLocation,
                initialCategory, initialOrganizer, initialSource, EventStatus.DRAFT, initialTime, initialTime, null);
    }

    @Test
    void changeDescription() {
        CatalogEvent event = createDraftEvent();
        EventDescription newDesc = new EventDescription("Nowy zaktualizowany opis");

        event.changeDescription(newDesc);

        assertThat(event.description()).isEqualTo(newDesc);
        assertThat(event.updatedAt()).isAfter(initialTime);
    }

    @Test
    void changeDate() {
        CatalogEvent event = createDraftEvent();
        EventDate newDate = mock(EventDate.class);

        event.changeDate(newDate);

        assertThat(event.date()).isEqualTo(newDate);
        assertThat(event.updatedAt()).isAfter(initialTime);
    }

    @Test
    void changeLocation() {
        CatalogEvent event = createDraftEvent();
        Location newLocation = mock(Location.class);

        event.changeLocation(newLocation);

        assertThat(event.location()).isEqualTo(newLocation);
        assertThat(event.updatedAt()).isAfter(initialTime);
    }

    @Test
    void changeCategory() {
        CatalogEvent event = createDraftEvent();
        EventCategory newCategory = EventCategory.fromText("KINO");

        // 1. Zmiana na konkretną kategorię
        event.changeCategory(newCategory);
        assertThat(event.category()).isEqualTo(newCategory);
        assertThat(event.updatedAt()).isAfter(initialTime);

        // 2. Zmiana na null (powinno ustawić INNE)
        event.changeCategory(null);
        assertThat(event.category()).isEqualTo(EventCategory.INNE);
    }

    @Test
    void update() {
        CatalogEvent event = createDraftEvent();
        EventName newName = new EventName("Nowa Nazwa");
        EventDescription newDesc = new EventDescription("Nowy Opis");
        EventDate newDate = mock(EventDate.class);
        Location newLoc = mock(Location.class);
        Organizer newOrg = mock(Organizer.class);

        event.update(newName, newDesc, newDate, newLoc, null, newOrg);

        assertThat(event.name()).isEqualTo(newName);
        assertThat(event.description()).isEqualTo(newDesc);
        assertThat(event.date()).isEqualTo(newDate);
        assertThat(event.location()).isEqualTo(newLoc);
        assertThat(event.category()).isEqualTo(EventCategory.INNE); // z null na INNE
        assertThat(event.organizer()).isEqualTo(newOrg);
        assertThat(event.updatedAt()).isAfter(initialTime);
    }

    @Test
    void publish() {
        // Scenariusz 1: Sukces (Wrocław, komplet danych, status DRAFT)
        CatalogEvent event = createDraftEvent();
        when(initialLocation.isInWroclaw()).thenReturn(true);

        event.publish();

        assertThat(event.status()).isEqualTo(EventStatus.PUBLISHED);
        assertThat(event.updatedAt()).isAfter(initialTime);

        // Scenariusz 2: Błąd - Status CANCELLED
        CatalogEvent cancelledEvent = new CatalogEvent(id, initialName, initialDescription, initialDate, initialLocation,
                initialCategory, initialOrganizer, initialSource, EventStatus.CANCELLED, initialTime, initialTime, "Powód");
        assertThatThrownBy(cancelledEvent::publish)
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Wydarzenie anulowane nie powinno być ponownie publikowane");

        // Scenariusz 3: Błąd - Status ARCHIVED
        CatalogEvent archivedEvent = new CatalogEvent(id, initialName, initialDescription, initialDate, initialLocation,
                initialCategory, initialOrganizer, initialSource, EventStatus.ARCHIVED, initialTime, initialTime, null);
        assertThatThrownBy(archivedEvent::publish)
                .isInstanceOf(BusinessException.class)
                .hasMessage("Wydarzenie archiwalne nie może zostać opublikowane");

        // Scenariusz 4: Błąd - Brak nazwy
        CatalogEvent eventNoName = new CatalogEvent(id, null, initialDescription, initialDate, initialLocation,
                initialCategory, initialOrganizer, initialSource, EventStatus.DRAFT, initialTime, initialTime, null);
        assertThatThrownBy(eventNoName::publish)
                .isInstanceOf(BusinessException.class)
                .hasMessage("Wydarzenie nie posiada kompletu danych");

        // Scenariusz 5: Błąd - Lokalizacja poza Wrocławiem
        CatalogEvent foreignEvent = createDraftEvent();
        when(initialLocation.isInWroclaw()).thenReturn(false);
        assertThatThrownBy(foreignEvent::publish)
                .isInstanceOf(BusinessException.class)
                .hasMessage("Katalog przechowuje tylko wydarzenia we Wrocławiu");
    }

    @Test
    void hide() {
        // 1. Ukrywanie ze statusu DRAFT (powinno zmienić status)
        CatalogEvent event = createDraftEvent();
        event.hide();
        assertThat(event.status()).isEqualTo(EventStatus.HIDDEN);
        assertThat(event.updatedAt()).isAfter(initialTime);

        // 2. Ukrywanie ze statusu ARCHIVED (nie powinno zmienić statusu ani czasu)
        CatalogEvent archivedEvent = new CatalogEvent(id, initialName, initialDescription, initialDate, initialLocation,
                initialCategory, initialOrganizer, initialSource, EventStatus.ARCHIVED, initialTime, initialTime, null);
        archivedEvent.hide();
        assertThat(archivedEvent.status()).isEqualTo(EventStatus.ARCHIVED);
        assertThat(archivedEvent.updatedAt()).isEqualTo(initialTime);
    }

    @Test
    void cancel() {
        CatalogEvent event = createDraftEvent();

        // 1. Anulowanie z podaniem powodu
        event.cancel("Brak zgody na hałas");
        assertThat(event.status()).isEqualTo(EventStatus.CANCELLED);
        assertThat(event.cancelReason()).isEqualTo("Brak zgody na hałas");
        assertThat(event.updatedAt()).isAfter(initialTime);

        // 2. Anulowanie z nullem (powinien być pusty ciąg znaków)
        CatalogEvent event2 = createDraftEvent();
        event2.cancel(null);
        assertThat(event2.cancelReason()).isEqualTo("");
    }

    @Test
    void archive() {
        CatalogEvent event = createDraftEvent();

        event.archive();

        assertThat(event.status()).isEqualTo(EventStatus.ARCHIVED);
        assertThat(event.updatedAt()).isAfter(initialTime);
    }
}
