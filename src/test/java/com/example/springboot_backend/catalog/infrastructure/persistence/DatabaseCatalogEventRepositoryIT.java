package com.example.springboot_backend.catalog.infrastructure.persistence;

import com.example.springboot_backend.catalog.domain.model.CatalogEvent;
import com.example.springboot_backend.catalog.domain.model.EventCategory;
import com.example.springboot_backend.catalog.domain.model.EventStatus;
import com.example.springboot_backend.catalog.domain.valueobject.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@WebMvcTest(DatabaseCatalogEventRepository.class)
class DatabaseCatalogEventRepositoryIT {

    @MockitoBean
    private SpringDataJpaCatalogEventRepository jpaRepository;

    private DatabaseCatalogEventRepository repository;

    @BeforeEach
    void setUp() {
        repository = new DatabaseCatalogEventRepository(jpaRepository);
    }

    @Test
    void save() {
        // given
        CatalogEvent event = createCatalogEvent(UUID.randomUUID(), "Test Event", EventStatus.DRAFT);
        JpaCatalogEventEntity entity = toEntity(event);
        when(jpaRepository.save(any())).thenReturn(entity);

        // when
        CatalogEvent saved = repository.save(event);

        // then
        assertNotNull(saved);
        assertEquals(event.id(), saved.id());
        assertEquals(event.name().value(), saved.name().value());
        verify(jpaRepository).save(any());
    }

    @Test
    void saveAll() {
        // given
        CatalogEvent event1 = createCatalogEvent(UUID.randomUUID(), "Event 1", EventStatus.DRAFT);
        CatalogEvent event2 = createCatalogEvent(UUID.randomUUID(), "Event 2", EventStatus.DRAFT);
        when(jpaRepository.saveAll(any())).thenReturn(List.of(toEntity(event1), toEntity(event2)));

        // when
        List<CatalogEvent> savedEvents = repository.saveAll(List.of(event1, event2));

        // then
        assertEquals(2, savedEvents.size());
        verify(jpaRepository).saveAll(any());
    }

    @Test
    void findById() {
        // given
        UUID id = UUID.randomUUID();
        CatalogEvent event = createCatalogEvent(id, "Found Event", EventStatus.PUBLISHED);
        when(jpaRepository.findById(id)).thenReturn(Optional.of(toEntity(event)));

        // when
        Optional<CatalogEvent> found = repository.findById(CatalogEventId.of(id));

        // then
        assertTrue(found.isPresent());
        assertEquals("Found Event", found.get().name().value());
    }

    @Test
    void findPublished() {
        // given
        CatalogEvent event = createCatalogEvent(UUID.randomUUID(), "Published Event", EventStatus.PUBLISHED);
        when(jpaRepository.findByStatus(EventStatus.PUBLISHED)).thenReturn(List.of(toEntity(event)));

        // when
        List<CatalogEvent> published = repository.findPublished();

        // then
        assertEquals(1, published.size());
        assertEquals(EventStatus.PUBLISHED, published.get(0).status());
    }

    @Test
    void findForIndexing() {
        // given
        CatalogEvent event = createCatalogEvent(UUID.randomUUID(), "For Indexing", EventStatus.PUBLISHED);
        when(jpaRepository.findByStatus(EventStatus.PUBLISHED)).thenReturn(List.of(toEntity(event)));

        // when
        List<CatalogEvent> forIndexing = repository.findForIndexing();

        // then
        assertEquals(1, forIndexing.size());
        verify(jpaRepository).findByStatus(EventStatus.PUBLISHED);
    }

    @Test
    void existsById() {
        // given
        UUID id = UUID.randomUUID();
        when(jpaRepository.existsById(id)).thenReturn(true);

        // when
        boolean exists = repository.existsById(CatalogEventId.of(id));

        // then
        assertTrue(exists);
    }

    @Test
    void delete() {
        // given
        UUID id = UUID.randomUUID();

        // when
        repository.delete(CatalogEventId.of(id));

        // then
        verify(jpaRepository).deleteById(id);
    }

    private CatalogEvent createCatalogEvent(UUID id, String name, EventStatus status) {
        return new CatalogEvent(
                CatalogEventId.of(id),
                new EventName(name),
                new EventDescription("Description"),
                new EventDate(Instant.now(), Instant.now().plusSeconds(3600)),
                new Location("Place", new Address("Street", "1", "Wroclaw"), null),
                EventCategory.INNE,
                new Organizer("Organizer", "http://organizer.com"),
                new EventSource(SourceType.ADMINISTRATOR, "Address"),
                status,
                Instant.now(),
                Instant.now(),
                null
        );
    }

    private JpaCatalogEventEntity toEntity(CatalogEvent e) {
        return new JpaCatalogEventEntity(
                e.id().value(),
                e.name().value(),
                e.description().value(),
                e.location().placeName(),
                e.location().city(),
                e.location().addressText(),
                e.date().start(),
                e.date().end(),
                e.category(),
                e.organizer().name(),
                e.organizer().website(),
                e.source().type(),
                e.source().sourceAddress(),
                e.status(),
                e.createdAt(),
                e.updatedAt(),
                e.cancelReason()
        );
    }
}