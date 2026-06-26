package com.example.springboot_backend.notification.domain.model;

import org.junit.jupiter.api.Test;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class InformatorTest {

    @Test
    void prepare() {
        // given
        UUID userId = UUID.randomUUID();
        UUID eventId = UUID.randomUUID();
        UUID notificationId = UUID.randomUUID();
        String eventTitle = "Koncert Rockowy we Wrocławiu";
        Instant beforeCreation = Instant.now();

        // when
        Informator result = Informator.prepare(userId, eventId, notificationId, eventTitle);

        // then
        assertThat(result).isNotNull();
        assertThat(result.id()).isNotNull();
        assertThat(result.userId()).isEqualTo(userId);
        assertThat(result.eventId()).isEqualTo(eventId);
        assertThat(result.notificationId()).isEqualTo(notificationId);

        // Weryfikacja formatowania tekstu wiadomości
        assertThat(result.message()).isEqualTo("Powiadomienie o wydarzeniu 'Koncert Rockowy we Wrocławiu' zostało przygotowane/wysłane.");

        // Weryfikacja czasu utworzenia w oknie wykonania testu
        assertThat(result.createdAt()).isAfterOrEqualTo(beforeCreation);
        assertThat(result.createdAt()).isBeforeOrEqualTo(Instant.now());
    }
}
