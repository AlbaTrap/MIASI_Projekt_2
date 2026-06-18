package com.example.springboot_backend.notification.application.service;
import com.example.springboot_backend.notification.domain.valueobject.NotificationChannel;
import org.springframework.stereotype.Service;
@Service
public class ChooseNotificationChannelApplicationService {
    public NotificationChannel choose(NotificationChannel requested) { return requested == null ? NotificationChannel.EMAIL : requested; }
}
