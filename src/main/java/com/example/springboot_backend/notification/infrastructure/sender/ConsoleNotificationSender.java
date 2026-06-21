package com.example.springboot_backend.notification.infrastructure.sender;
import com.example.springboot_backend.account.application.dto.UserContactData;
import com.example.springboot_backend.catalog.application.dto.EventSnapshot;
import com.example.springboot_backend.notification.domain.model.Notification;
import com.example.springboot_backend.notification.domain.port.NotificationSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
@Component
public class ConsoleNotificationSender implements NotificationSender {
    private static final Logger log = LoggerFactory.getLogger(ConsoleNotificationSender.class);
    @Override public void send(Notification notification, EventSnapshot event, UserContactData userContactData) {
        log.info("[DEMO {}] To: {}, Event: {}, Subject: {}, Message: {}", notification.channel(), userContactData.email(), event.title(), notification.content().subject(), notification.content().message());
    }
}
