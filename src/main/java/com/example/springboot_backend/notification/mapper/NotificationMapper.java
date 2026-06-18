package com.example.springboot_backend.notification.mapper;
import com.example.springboot_backend.notification.application.dto.*;
import com.example.springboot_backend.notification.domain.model.*;
public final class NotificationMapper {
    private NotificationMapper() {}
    public static NotificationDto toDto(Notification n) { return new NotificationDto(n.id().value(), n.userId(), n.eventId(), n.channel(), n.status(), n.content().subject(), n.content().message(), n.createdAt(), n.sentAt(), n.failureReason()); }
    public static InformatorDto toDto(Informator i) { return new InformatorDto(i.id().value(), i.userId(), i.eventId(), i.notificationId(), i.message(), i.createdAt()); }
}
