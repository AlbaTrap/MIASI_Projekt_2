package com.example.springboot_backend.favorite.infrastructure.persistence;

import com.example.springboot_backend.favorite.domain.model.FavoriteEvent;
import com.example.springboot_backend.favorite.domain.valueobject.FavoriteEventId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@WebMvcTest(DatabaseFavoriteEventRepository.class)
class DatabaseFavoriteEventRepositoryIT {

    private DatabaseFavoriteEventRepository databaseRepository;

    @MockitoBean
    private SpringDataJpaFavoriteEventRepository jpaRepository;

    @BeforeEach
    void setUp() {
        databaseRepository = new DatabaseFavoriteEventRepository(jpaRepository);
    }

    @Test
    void save() {
        // given
        FavoriteEvent favoriteEvent = createFavoriteEvent(UUID.randomUUID(), UUID.randomUUID());
        JpaFavoriteEventEntity entity = toEntity(favoriteEvent);
        when(jpaRepository.save(any())).thenReturn(entity);

        // when
        FavoriteEvent saved = databaseRepository.save(favoriteEvent);

        // then
        assertThat(saved).isNotNull();
        assertThat(saved.id()).isEqualTo(favoriteEvent.id());
        verify(jpaRepository).save(any());
    }

    @Test
    void findByUserId() {
        // given
        UUID userId = UUID.randomUUID();
        JpaFavoriteEventEntity entity = new JpaFavoriteEventEntity(UUID.randomUUID(), userId, UUID.randomUUID(), Instant.now());
        when(jpaRepository.findByUserId(userId)).thenReturn(List.of(entity));

        // when
        List<FavoriteEvent> results = databaseRepository.findByUserId(userId);

        // then
        assertThat(results).hasSize(1);
        assertThat(results.get(0).userId()).isEqualTo(userId);
    }

    @Test
    void findByUserIdAndEventId() {
        // given
        UUID userId = UUID.randomUUID();
        UUID eventId = UUID.randomUUID();
        JpaFavoriteEventEntity entity = new JpaFavoriteEventEntity(UUID.randomUUID(), userId, eventId, Instant.now());
        when(jpaRepository.findByUserIdAndEventId(userId, eventId)).thenReturn(Optional.of(entity));

        // when
        Optional<FavoriteEvent> result = databaseRepository.findByUserIdAndEventId(userId, eventId);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().userId()).isEqualTo(userId);
        assertThat(result.get().eventId()).isEqualTo(eventId);
    }

    @Test
    void existsByUserIdAndEventId() {
        // given
        UUID userId = UUID.randomUUID();
        UUID eventId = UUID.randomUUID();
        when(jpaRepository.existsByUserIdAndEventId(userId, eventId)).thenReturn(true);

        // when
        boolean exists = databaseRepository.existsByUserIdAndEventId(userId, eventId);

        // then
        assertThat(exists).isTrue();
    }

    @Test
    void delete() {
        // given
        FavoriteEventId id = FavoriteEventId.newId();
        doNothing().when(jpaRepository).deleteById(id.value());

        // when
        databaseRepository.delete(id);

        // then
        verify(jpaRepository).deleteById(id.value());
    }

    private FavoriteEvent createFavoriteEvent(UUID userId, UUID eventId) {
        return new FavoriteEvent(FavoriteEventId.newId(), userId, eventId, Instant.now());
    }

    private JpaFavoriteEventEntity toEntity(FavoriteEvent f) {
        return new JpaFavoriteEventEntity(f.id().value(), f.userId(), f.eventId(), f.addedAt());
    }
}
