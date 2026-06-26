package com.example.springboot_backend.notification.application.service;

import com.example.springboot_backend.notification.domain.valueobject.NotificationChannel;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ChooseNotificationChannelApplicationServiceTest {

    private final ChooseNotificationChannelApplicationService service = new ChooseNotificationChannelApplicationService();

    @Test
    void choose() {
        // 1. Scenariusz: Przekazano konkretny kanał powiadomień (np. SMS lub PUSH)
        // Zakładam, że NotificationChannel to enum posiadający przynajmniej wartość EMAIL i inną, np. SMS
        NotificationChannel requestedChannel = NotificationChannel.SMS;

        NotificationChannel resultSpecific = service.choose(requestedChannel);

        assertThat(resultSpecific).isEqualTo(NotificationChannel.SMS);

        // 2. Scenariusz: Przekazano wartość null (powinien nastąpić fallback do EMAIL)
        NotificationChannel resultNull = service.choose(null);

        assertThat(resultNull).isEqualTo(NotificationChannel.EMAIL);
    }
}
