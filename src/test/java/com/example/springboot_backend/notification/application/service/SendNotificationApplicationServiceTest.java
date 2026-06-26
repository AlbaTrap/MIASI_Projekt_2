package com.example.springboot_backend.notification.application.service;

import com.example.springboot_backend.account.application.port.AccountAccessPort;
import com.example.springboot_backend.catalog.application.dto.CatalogEventDto;
import com.example.springboot_backend.catalog.application.port.EventAvailabilityPort;
import com.example.springboot_backend.favorite.application.port.FavoriteEventsAccessPort;
import com.example.springboot_backend.notification.application.command.SendNotificationCommand;
import com.example.springboot_backend.notification.application.dto.NotificationDto;
import com.example.springboot_backend.notification.domain.event.NotificationCreatedEvent;
import com.example.springboot_backend.notification.domain.event.NotificationFailedEvent;
import com.example.springboot_backend.notification.domain.event.NotificationSentEvent;
import com.example.springboot_backend.notification.domain.model.Notification;
import com.example.springboot_backend.notification.domain.port.NotificationSender;
import com.example.springboot_backend.notification.domain.repository.NotificationRepository;
import com.example.springboot_backend.notification.domain.service.NotificationContentFactory;
import com.example.springboot_backend.notification.domain.service.NotificationPolicy;
import com.example.springboot_backend.notification.domain.valueobject.NotificationChannel;
import com.example.springboot_backend.shared.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SendNotificationApplicationServiceTest {

    @Mock private AccountAccessPort accountAccessPort;
    @Mock private EventAvailabilityPort eventAvailabilityPort;
    @Mock private FavoriteEventsAccessPort favoriteEventsAccessPort;
    @Mock private ChooseNotificationChannelApplicationService channelService;
    @Mock private NotificationContentFactory contentFactory;
    @Mock private NotificationPolicy policy;
    @Mock private NotificationSender sender;
    @Mock private NotificationRepository notificationRepository;
    @Mock private DomainEventPublisher publisher;

    @InjectMocks
    private SendNotificationApplicationService service;

    private UUID userId;
    private UUID eventId;
    private UUID notificationId;
    private SendNotificationCommand command;
    private AccountAccessPort.UserContactData contactData; // Zakładam istnienie tego typu/rekordu w porcie
    private CatalogEventDto eventDto;
    private Notification mockNotification;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        eventId = UUID.randomUUID();
        notificationId = UUID.randomUUID();
        command = new SendNotificationCommand("valid-token", eventId, NotificationChannel.EMAIL);

        // Mockowanie struktur danych z portów zewnętrznych
        contactData = mock(AccountAccessPort.UserContactData.class);
        eventDto = mock(CatalogEventDto.class);

        // Deep stubs pozwala na bezbłędne wywołanie notification.id().value()
        mockNotification = mock(Notification.class, Mockito.RETURNS_DEEP_STUBS);
        lenient().when(mockNotification.id().value()).thenReturn(notificationId);

        // Konfiguracja domyślnych, poprawnych odpowiedzi mocków dla większości scenariuszy
        lenient().when(accountAccessPort.findAccountIdByAccessToken(any())).thenReturn(Optional.of(userId));
        lenient().when(accountAccessPort.getUserContactData(userId)).thenReturn(Optional.of(contactData));
        lenient().when(eventAvailabilityPort.getEventDetails(eventId)).thenReturn(Optional.of(eventDto));
        lenient().when(favoriteEventsAccessPort.isFavorite(userId, eventId)).thenReturn(true);
        lenient().when(channelService.choose(any())).thenReturn(NotificationChannel.EMAIL);
        lenient().when(notificationRepository.save(any(Notification.class))).thenReturn(mockNotification);
    }

    @Test
    void send() {
        // =========================================================================
        // SCENARIUSZ 1: Pełny sukces (Wysłano powiadomienie bez błędów)
        // =========================================================================
        NotificationDto result = service.send(command);

        assertThat(result).isNotNull();
        verify(policy).checkCanSend(true, true);
        verify(mockNotification).markAsSent();
        verify(notificationRepository, times(2)).save(any(Notification.class));
        verify(publisher).publish(any(NotificationCreatedEvent.class));
        verify(publisher).publish(any(NotificationSentEvent.class));

        // =========================================================================
        // SCENARIUSZ 2: Wyjątek podczas wysyłki (Blok catch - Zmiana statusu na Failed)
        // =========================================================================
        reset(mockNotification, publisher, notificationRepository);
        when(notificationRepository.save(any(Notification.class))).thenReturn(mockNotification);

        // Symulacja rzucenia błędu przez infrastrukturę wysyłkową (np. awaria serwera SMTP)
        doThrow(new RuntimeException("SMTP Connection Timeout")).when(sender).send(any(), any(), any());

        service.send(command);

        verify(mockNotification).markAsFailed("SMTP Connection Timeout");
        verify(publisher).publish(any(NotificationCreatedEvent.class));
        verify(publisher).publish(any(NotificationFailedEvent.class));

        // =========================================================================
        // SCENARIUSZ 3: Walidacja biznesowa (Żądanie SMS, ale brak numeru telefonu)
        // =========================================================================
        reset(publisher, notificationRepository);
        SendNotificationCommand smsCommand = new SendNotificationCommand("valid-token", eventId, NotificationChannel.SMS);

        when(channelService.choose(NotificationChannel.SMS)).thenReturn(NotificationChannel.SMS);
        when(contactData.phoneNumber()).thenReturn(""); // Pusty numer telefonu

        assertThatThrownBy(() -> service.send(smsCommand))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Powiadomienia SMS nie są dostępne bez numeru telefonu");

        // Weryfikacja, że w przypadku błędu walidacji nie podjęto próby zapisu ani wysyłki
        verifyNoInteractions(sender);
        verifyNoMoreInteractions(notificationRepository);
    }
}
