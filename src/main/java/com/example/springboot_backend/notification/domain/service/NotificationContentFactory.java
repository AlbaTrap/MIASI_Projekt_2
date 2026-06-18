package com.example.springboot_backend.notification.domain.service;
import com.example.springboot_backend.event.application.dto.EventSnapshot;
import com.example.springboot_backend.notification.domain.valueobject.NotificationChannel;
import com.example.springboot_backend.notification.domain.valueobject.NotificationContent;
import org.springframework.stereotype.Component;
@Component
public class NotificationContentFactory {
    public NotificationContent create(EventSnapshot event, NotificationChannel channel) {
        String subject = "Przypomnienie o wydarzeniu: " + event.title();
        String message = channel == NotificationChannel.SMS
                ? "Wydarzenie: " + event.title() + ", " + event.startDate()
                : "Cześć! Przypominamy o wydarzeniu " + event.title() + " we Wrocławiu. Start: " + event.startDate() + ", adres: " + event.address();
        return new NotificationContent(subject, message);
    }
}
