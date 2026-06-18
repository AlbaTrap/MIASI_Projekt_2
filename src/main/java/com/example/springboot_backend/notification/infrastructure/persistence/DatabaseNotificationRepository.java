package com.example.springboot_backend.notification.infrastructure.persistence;
import com.example.springboot_backend.notification.domain.model.Notification;
import com.example.springboot_backend.notification.domain.repository.NotificationRepository;
import com.example.springboot_backend.notification.domain.valueobject.*;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
public class DatabaseNotificationRepository implements NotificationRepository {
    private final SpringDataJpaNotificationRepository repository;
    public DatabaseNotificationRepository(SpringDataJpaNotificationRepository repository) { this.repository=repository; }
    @Override public Notification save(Notification n) { return toDomain(repository.save(toJpa(n))); }
    @Override public Optional<Notification> findById(NotificationId id) { return repository.findById(id.value()).map(this::toDomain); }
    @Override public List<Notification> findByUserId(UUID userId) { return repository.findByUserId(userId).stream().map(this::toDomain).toList(); }
    private JpaNotificationEntity toJpa(Notification n) { return new JpaNotificationEntity(n.id().value(), n.userId(), n.eventId(), n.channel(), n.status(), n.content().subject(), n.content().message(), n.createdAt(), n.sentAt(), n.failureReason()); }
    private Notification toDomain(JpaNotificationEntity e) { return new Notification(NotificationId.of(e.getId()), e.getUserId(), e.getEventId(), e.getChannel(), new NotificationContent(e.getSubject(), e.getMessage()), e.getCreatedAt(), e.getSentAt(), e.getStatus(), e.getFailureReason()); }
}
