package com.example.springboot_backend.importevents.infrastructure.persistence;

import com.example.springboot_backend.importevents.domain.model.ImportSource;
import com.example.springboot_backend.importevents.domain.model.RawEvent;
import com.example.springboot_backend.importevents.domain.valueobject.RawEventId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@WebMvcTest(DatabaseRawEventRepository.class)
class DatabaseRawEventRepositoryIT {

    @Autowired
    private DatabaseRawEventRepository databaseRawEventRepository;

    @MockitoBean(name = "importEventsSpringDataJpaRawEventRepository")
    private SpringDataJpaRawEventRepository springDataJpaRawEventRepository;

    @Test
    void save() {
        // given
        RawEvent rawEvent = createRawEvent();
        JpaRawEventEntity entity = toEntity(rawEvent);
        when(springDataJpaRawEventRepository.save(any(JpaRawEventEntity.class))).thenReturn(entity);

        // when
        RawEvent saved = databaseRawEventRepository.save(rawEvent);

        // then
        assertNotNull(saved);
        assertEquals(rawEvent.id(), saved.id());
        verify(springDataJpaRawEventRepository).save(any(JpaRawEventEntity.class));
    }

    @Test
    void saveAll() {
        // given
        RawEvent rawEvent1 = createRawEvent();
        RawEvent rawEvent2 = createRawEvent();
        List<RawEvent> rawEvents = List.of(rawEvent1, rawEvent2);
        List<JpaRawEventEntity> entities = rawEvents.stream().map(this::toEntity).toList();
        when(springDataJpaRawEventRepository.saveAll(any())).thenReturn(entities);

        // when
        List<RawEvent> savedEvents = databaseRawEventRepository.saveAll(rawEvents);

        // then
        assertEquals(2, savedEvents.size());
        verify(springDataJpaRawEventRepository).saveAll(any());
    }

    @Test
    void findById() {
        // given
        RawEvent rawEvent = createRawEvent();
        JpaRawEventEntity entity = toEntity(rawEvent);
        when(springDataJpaRawEventRepository.findById(rawEvent.id().value())).thenReturn(Optional.of(entity));

        // when
        Optional<RawEvent> found = databaseRawEventRepository.findById(rawEvent.id());

        // then
        assertTrue(found.isPresent());
        assertEquals(rawEvent.id(), found.get().id());
    }

    @Test
    void findUnprocessed() {
        // given
        RawEvent rawEvent = createRawEvent();
        JpaRawEventEntity entity = toEntity(rawEvent);
        when(springDataJpaRawEventRepository.findByProcessedFalse()).thenReturn(List.of(entity));

        // when
        List<RawEvent> unprocessed = databaseRawEventRepository.findUnprocessed();

        // then
        assertEquals(1, unprocessed.size());
        assertFalse(unprocessed.get(0).processed());
    }

    private RawEvent createRawEvent() {
        return new RawEvent(
                RawEventId.newId(),
                ImportSource.API,
                "Title",
                "Description",
                "Location",
                "2023-10-10",
                "Category",
                Instant.now(),
                false
        );
    }

    private JpaRawEventEntity toEntity(RawEvent r) {
        return new JpaRawEventEntity(
                r.id().value(),
                r.source(),
                r.rawTitle(),
                r.rawDescription(),
                r.rawLocation(),
                r.rawDate(),
                r.rawCategory(),
                r.fetchedAt(),
                r.processed()
        );
    }
}