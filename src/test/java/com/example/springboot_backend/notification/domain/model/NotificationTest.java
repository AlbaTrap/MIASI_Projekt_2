package com.example.springboot_backend.notification.domain.model;

import com.example.springboot_backend.notification.domain.valueobject.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class NotificationTest {

    private UUID userId;
    private UUID eventId;
    private NotificationChannel channel;
    private NotificationContent content;
    private Instant initialTime;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        eventId = UUID.randomUUID();
        channel = NotificationChannel.EMAIL;
        content = mock(NotificationContent.class);
        initialTime = Instant.now().minusSeconds(10); // Czas w przeszłości do wykrywania zmian
    }

    private Notification createDefaultNotification() {
        return new Notification(
                NotificationId.newId(), userId, eventId, channel, content,
                initialTime, null, NotificationStatus.CREATED, null
        );
    }

    @Test
    void create() {
        // given
        Instant beforeCreation = Instant.now();

        // when
        Notification result = Notification.create(userId, eventId, channel, content);

        // then
        assertThat(result).isNotNull();
        assertThat(result.id()).isNotNull();
        assertThat(result.userId()).isEqualTo(userId);
        assertThat(result.eventId()).isEqualTo(eventId);
        assertThat(result.channel()).isEqualTo(channel);
        assertThat(result.content()).isEqualTo(content);
        assertThat(result.status()).isEqualTo(NotificationStatus.CREATED);
        assertThat(result.sentAt()).isNull();
        assertThat(result.failureReason()).isNull();

        // Weryfikacja poprawnego czasu utworzenia
        assertThat(result.createdAt()).isAfterOrEqualTo(beforeCreation);
        assertThat(result.createdAt()).isBeforeOrEqualTo(Instant.now());
    }

    @Test
    void markAsSent() {
        // given
        Notification notification = createDefaultNotification();
        assertThat(notification.status()).isEqualTo(NotificationStatus.CREATED);
        assertThat(notification.sentAt()).isNull();

        // Dodatkowo ustawiamy powód awarii, aby sprawdzić czy metoda go wyczyści (zeruje do null)
        Notification notificationWithOldFailure = new Notification(
                NotificationId.newId(), userId, eventId, channel, content,
                initialTime, null, NotificationStatus.FAILED, "Stary błąd sieci"
        );

        // when & then (Przypadek 1: Normalne oznaczenie jako wysłane)
        notification.markAsSent();
        assertThat(notification.status()).isEqualTo(NotificationStatus.SENT);
        assertThat(notification.sentAt()).isAfter(initialTime);
        assertThat(notification.failureReason()).isNull();

        // when & then (Przypadek 2: Wysłanie po wcześniejszym błędzie powinno wyczyścić failureReason)
        notificationWithOldFailure.markAsSent();
        assertThat(notificationWithOldFailure.status()).isEqualTo(NotificationStatus.SENT);
        assertThat(notificationWithOldFailure.failureReason()).isNull();
    }

    @Test
    void markAsFailed() {
        // given
        Notification notification = createDefaultNotification();
        String errorReason = "Błędny adres e-mail odbiorcy";

        // when
        notification.markAsFailed(errorReason);

        // then
        assertThat(notification.status()).isEqualTo(NotificationStatus.FAILED);
        assertThat(notification.failureReason()).isEqualTo(errorReason);
        assertThat(notification.sentAt()).isNull(); // Status błędu nie powinien ustawiać czasu wysyłki
    }
}
