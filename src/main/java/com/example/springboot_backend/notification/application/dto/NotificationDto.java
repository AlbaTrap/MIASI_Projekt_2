package com.example.springboot_backend.notification.application.dto;
import com.example.springboot_backend.notification.domain.model.NotificationStatus;
import com.example.springboot_backend.notification.domain.valueobject.NotificationChannel;
import java.time.Instant;
import java.util.UUID;
public record NotificationDto(UUID id, UUID userId, UUID eventId, NotificationChannel channel, NotificationStatus status, String subject, String message, Instant createdAt, Instant sentAt, String failureReason) { }
