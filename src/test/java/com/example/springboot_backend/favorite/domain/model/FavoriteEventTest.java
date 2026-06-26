package com.example.springboot_backend.favorite.domain.model;

import org.junit.jupiter.api.Test;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class FavoriteEventTest {

    @Test
    void create() {
        // given
        UUID userId = UUID.randomUUID();
        UUID eventId = UUID.randomUUID();
        Instant beforeCreation = Instant.now();

        // when
        FavoriteEvent result = FavoriteEvent.create(userId, eventId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.id()).isNotNull();
        assertThat(result.userId()).isEqualTo(userId);
        assertThat(result.eventId()).isEqualTo(eventId);

        // Weryfikacja czy czas zapisu addedAt jest poprawny i mieści się w oknie czasowym testu
        assertThat(result.addedAt()).isAfterOrEqualTo(beforeCreation);
        assertThat(result.addedAt()).isBeforeOrEqualTo(Instant.now());
    }
}
