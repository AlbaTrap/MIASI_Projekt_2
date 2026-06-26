package com.example.springboot_backend.notification.domain.service;

import com.example.springboot_backend.catalog.application.dto.EventSnapshot;
import com.example.springboot_backend.notification.domain.valueobject.NotificationChannel;
import com.example.springboot_backend.notification.domain.valueobject.NotificationContent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class NotificationContentFactoryTest {

    private final NotificationContentFactory factory = new NotificationContentFactory();
    private EventSnapshot mockEvent;

    @BeforeEach
    void setUp() {
        // Mockujemy obiekt migawki wydarzenia (EventSnapshot)
        mockEvent = mock(EventSnapshot.class);
        when(mockEvent.title()).thenReturn("Koncert Jazzowy");
        when(mockEvent.startDate()).thenReturn("2026-07-15 19:00");
        when(mockEvent.address()).thenReturn("Rynek 1, Wrocław");
    }

    @Test
    void create() {
        // =========================================================================
        // SCENARIUSZ 1: Generowanie treści dla kanału SMS (krótka treść)
        // =========================================================================
        NotificationContent smsResult = factory.create(mockEvent, NotificationChannel.SMS);

        assertThat(smsResult).isNotNull();
        assertThat(smsResult.subject()).isEqualTo("Przypomnienie o wydarzeniu: Koncert Jazzowy");
        assertThat(smsResult.message()).isEqualTo("Wydarzenie: Koncert Jazzowy, 2026-07-15 19:00");

        // =========================================================================
        // SCENARIUSZ 2: Generowanie treści dla kanału EMAIL (pełna treść)
        // =========================================================================
        NotificationContent emailResult = factory.create(mockEvent, NotificationChannel.EMAIL);

        assertThat(emailResult).isNotNull();
        assertThat(emailResult.subject()).isEqualTo("Przypomnienie o wydarzeniu: Koncert Jazzowy");
        assertThat(emailResult.message()).isEqualTo(
                "Cześć! Przypominamy o wydarzeniu Koncert Jazzowy we Wrocławiu. Start: 2026-07-15 19:00, adres: Rynek 1, Wrocław"
        );
    }
}
