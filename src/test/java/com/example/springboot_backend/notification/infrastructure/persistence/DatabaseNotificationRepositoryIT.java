package com.example.springboot_backend.notification.infrastructure.persistence;

import com.example.springboot_backend.notification.domain.model.Notification;
import com.example.springboot_backend.notification.domain.model.NotificationStatus;
import com.example.springboot_backend.notification.domain.valueobject.NotificationChannel;
import com.example.springboot_backend.notification.domain.valueobject.NotificationContent;
import com.example.springboot_backend.notification.domain.valueobject.NotificationId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebMvcTest(DatabaseNotificationRepository.class)
class DatabaseNotificationRepositoryIT {

    @Autowired
    private DatabaseNotificationRepository repository;

    @MockitoBean
    private SpringDataJpaNotificationRepository jpaRepository;

    private UUID userId;
    private UUID eventId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        eventId = UUID.randomUUID();
    }

    @Test
    void save() {
        // given
        Notification notification = createNotification();
        JpaNotificationEntity entity = toEntity(notification);
        when(jpaRepository.save(any(JpaNotificationEntity.class))).thenReturn(entity);

        // when
        Notification saved = repository.save(notification);

        // then
        assertThat(saved).isNotNull();
        assertThat(saved.id()).isEqualTo(notification.id());
        assertThat(saved.userId()).isEqualTo(userId);
    }

    @Test
    void findById() {
        // given
        Notification notification = createNotification();
        JpaNotificationEntity entity = toEntity(notification);
        when(jpaRepository.findById(notification.id().value())).thenReturn(Optional.of(entity));

        // when
        Optional<Notification> found = repository.findById(notification.id());

        // then
        assertThat(found).isPresent();
        assertThat(found.get().id()).isEqualTo(notification.id());
    }

    @Test
    void findByUserId() {
        // given
        Notification notification = createNotification();
        JpaNotificationEntity entity = toEntity(notification);
        when(jpaRepository.findByUserId(userId)).thenReturn(List.of(entity));

        // when
        List<Notification> result = repository.findByUserId(userId);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).userId()).isEqualTo(userId);
    }

    private Notification createNotification() {
        return new Notification(
                NotificationId.newId(),
                userId,
                eventId,
                NotificationChannel.EMAIL,
                new NotificationContent("Subject", "Message"),
                Instant.now(),
                null,
                NotificationStatus.CREATED,
                null
        );
    }

    private JpaNotificationEntity toEntity(Notification n) {
        return new JpaNotificationEntity(
                n.id().value(),
                n.userId(),
                n.eventId(),
                n.channel(),
                n.status(),
                n.content().subject(),
                n.content().message(),
                n.createdAt(),
                n.sentAt(),
                n.failureReason()
        );
    }
}