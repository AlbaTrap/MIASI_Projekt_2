package com.example.springboot_backend.catalog.application.service;

import com.example.springboot_backend.catalog.application.dto.CatalogEventDto;
import com.example.springboot_backend.catalog.application.dto.EventSnapshot;
import com.example.springboot_backend.catalog.domain.model.EventCategory;
import com.example.springboot_backend.catalog.domain.model.EventStatus;
import com.example.springboot_backend.catalog.domain.model.CatalogEvent;
import com.example.springboot_backend.catalog.domain.repository.CatalogEventRepository;
import com.example.springboot_backend.catalog.domain.valueobject.*;
import com.example.springboot_backend.shared.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventQueryServiceTest {

    @Mock
    private CatalogEventRepository repository;

    @InjectMocks
    private EventQueryService eventQueryService;

    private UUID eventId;
    private CatalogEventId catalogEventId;

    @BeforeEach
    void setUp() {
        eventId = UUID.randomUUID();
        catalogEventId = CatalogEventId.of(eventId);
    }

    @Test
    void getEvent_ShouldReturnDto_WhenEventExists() {
        // given
        CatalogEvent event = createEvent(EventStatus.DRAFT);
        when(repository.findById(catalogEventId)).thenReturn(Optional.of(event));

        // when
        CatalogEventDto result = eventQueryService.getEvent(eventId);

        // then
        assertNotNull(result);
        assertEquals(eventId, result.id());
        assertEquals("Test Event", result.title());
    }

    @Test
    void getEvent_ShouldThrowNotFoundException_WhenEventDoesNotExist() {
        // given
        when(repository.findById(catalogEventId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(NotFoundException.class, () -> eventQueryService.getEvent(eventId));
    }

    @Test
    void getPublicEvent_ShouldReturnDto_WhenEventIsPublic() {
        // given
        CatalogEvent event = createEvent(EventStatus.PUBLISHED);
        when(repository.findById(catalogEventId)).thenReturn(Optional.of(event));

        // when
        CatalogEventDto result = eventQueryService.getPublicEvent(eventId);

        // then
        assertNotNull(result);
        assertEquals(EventStatus.PUBLISHED, result.status());
    }

    @Test
    void getPublicEvent_ShouldThrowNotFoundException_WhenEventIsDraft() {
        // given
        CatalogEvent event = createEvent(EventStatus.DRAFT);
        when(repository.findById(catalogEventId)).thenReturn(Optional.of(event));

        // when & then
        assertThrows(NotFoundException.class, () -> eventQueryService.getPublicEvent(eventId));
    }

    @Test
    void eventExists_ShouldReturnTrue_WhenExists() {
        // given
        when(repository.existsById(catalogEventId)).thenReturn(true);

        // when
        boolean result = eventQueryService.eventExists(eventId);

        // then
        assertTrue(result);
    }

    @Test
    void getEventStatus_ShouldReturnStatusName() {
        // given
        CatalogEvent event = createEvent(EventStatus.PUBLISHED);
        when(repository.findById(catalogEventId)).thenReturn(Optional.of(event));

        // when
        String status = eventQueryService.getEventStatus(eventId);

        // then
        assertEquals("PUBLISHED", status);
    }

    @Test
    void getPublishedEvents_ShouldReturnList() {
        // given
        CatalogEvent event = createEvent(EventStatus.PUBLISHED);
        when(repository.findPublished()).thenReturn(List.of(event));

        // when
        List<CatalogEventDto> results = eventQueryService.getPublishedEvents();

        // then
        assertEquals(1, results.size());
        assertEquals(EventStatus.PUBLISHED, results.get(0).status());
    }

    @Test
    void isEventAvailable_ShouldReturnTrue_WhenPublished() {
        // given
        CatalogEvent event = createEvent(EventStatus.PUBLISHED);
        when(repository.findById(catalogEventId)).thenReturn(Optional.of(event));

        // when
        boolean result = eventQueryService.isEventAvailable(eventId);

        // then
        assertTrue(result);
    }

    @Test
    void getEventDetails_ShouldReturnSnapshot_WhenPublished() {
        // given
        CatalogEvent event = createEvent(EventStatus.PUBLISHED);
        when(repository.findById(catalogEventId)).thenReturn(Optional.of(event));

        // when
        Optional<EventSnapshot> result = eventQueryService.getEventDetails(eventId);

        // then
        assertTrue(result.isPresent());
        assertEquals("Test Event", result.get().title());
    }

    @Test
    void getFavoriteEventDetails_ShouldReturnDto_WhenStatusIsAllowed() {
        // given
        CatalogEvent event = createEvent(EventStatus.CANCELLED);
        when(repository.findById(catalogEventId)).thenReturn(Optional.of(event));

        // when
        Optional<CatalogEventDto> result = eventQueryService.getFavoriteEventDetails(eventId);

        // then
        assertTrue(result.isPresent());
        assertEquals(EventStatus.CANCELLED, result.get().status());
    }

    @Test
    void getEventForIndex_ShouldReturnDto() {
        // given
        CatalogEvent event = createEvent(EventStatus.DRAFT);
        when(repository.findById(catalogEventId)).thenReturn(Optional.of(event));

        // when
        Optional<CatalogEventDto> result = eventQueryService.getEventForIndex(eventId);

        // then
        assertTrue(result.isPresent());
        assertEquals(eventId, result.get().id());
    }

    @Test
    void getEventsForIndexing_ShouldReturnList() {
        // given
        CatalogEvent event = createEvent(EventStatus.PUBLISHED);
        when(repository.findForIndexing()).thenReturn(List.of(event));

        // when
        List<CatalogEventDto> results = eventQueryService.getEventsForIndexing();

        // then
        assertEquals(1, results.size());
    }

    private CatalogEvent createEvent(EventStatus status) {
        Instant now = Instant.now();
        return new CatalogEvent(
                catalogEventId,
                new EventName("Test Event"),
                new EventDescription("Description"),
                new EventDate(now.plusSeconds(3600), now.plusSeconds(7200)),
                new Location("Venue", new Address("Street", "123", "Wrocław"), new Coordinates(0.0, 0.0)),
                EventCategory.INNE,
                new Organizer("Organizer", "website"),
                EventSource.administrator(),
                status,
                now,
                now,
                null
        );
    }
}