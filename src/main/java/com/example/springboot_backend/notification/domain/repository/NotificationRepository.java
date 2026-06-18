package com.example.springboot_backend.notification.domain.repository;
import com.example.springboot_backend.notification.domain.model.Notification;
import com.example.springboot_backend.notification.domain.valueobject.NotificationId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
public interface NotificationRepository {
    Notification save(Notification notification);
    Optional<Notification> findById(NotificationId id);
    List<Notification> findByUserId(UUID userId);
}
