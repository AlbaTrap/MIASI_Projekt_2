package com.example.springboot_backend.notification.application.command;
import com.example.springboot_backend.notification.domain.valueobject.NotificationChannel;
import java.util.UUID;
public record SendNotificationCommand(String accessToken, UUID eventId, NotificationChannel channel) { }
