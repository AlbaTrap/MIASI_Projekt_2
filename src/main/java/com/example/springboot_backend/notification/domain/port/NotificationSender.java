package com.example.springboot_backend.notification.domain.port;
import com.example.springboot_backend.account.application.dto.UserContactData;
import com.example.springboot_backend.event.application.dto.EventSnapshot;
import com.example.springboot_backend.notification.domain.model.Notification;
public interface NotificationSender {
    void send(Notification notification, EventSnapshot event, UserContactData userContactData);
}
