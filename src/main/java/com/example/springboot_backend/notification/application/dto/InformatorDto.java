package com.example.springboot_backend.notification.application.dto;
import java.time.Instant;
import java.util.UUID;
public record InformatorDto(UUID id, UUID userId, UUID eventId, UUID notificationId, String message, Instant createdAt) { }
