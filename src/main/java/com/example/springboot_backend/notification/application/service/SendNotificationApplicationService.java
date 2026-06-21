package com.example.springboot_backend.notification.application.service;
import com.example.springboot_backend.account.application.port.AccountAccessPort;
import com.example.springboot_backend.catalog.application.port.EventAvailabilityPort;
import com.example.springboot_backend.favorite.application.port.FavoriteEventsAccessPort;
import com.example.springboot_backend.notification.application.command.SendNotificationCommand;
import com.example.springboot_backend.notification.application.dto.NotificationDto;
import com.example.springboot_backend.notification.domain.event.*;
import com.example.springboot_backend.notification.domain.model.Notification;
import com.example.springboot_backend.notification.domain.port.NotificationSender;
import com.example.springboot_backend.notification.domain.repository.NotificationRepository;
import com.example.springboot_backend.notification.domain.service.NotificationContentFactory;
import com.example.springboot_backend.notification.domain.service.NotificationPolicy;
import com.example.springboot_backend.notification.mapper.NotificationMapper;
import com.example.springboot_backend.shared.event.DomainEventPublisher;
import com.example.springboot_backend.shared.exception.NotFoundException;
import com.example.springboot_backend.shared.exception.UnauthorizedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;

@Service
public class SendNotificationApplicationService {
    private final AccountAccessPort accountAccessPort;
    private final EventAvailabilityPort eventAvailabilityPort;
    private final FavoriteEventsAccessPort favoriteEventsAccessPort;
    private final ChooseNotificationChannelApplicationService channelService;
    private final NotificationContentFactory contentFactory;
    private final NotificationPolicy policy;
    private final NotificationSender sender;
    private final NotificationRepository notificationRepository;
    private final DomainEventPublisher publisher;
    public SendNotificationApplicationService(AccountAccessPort accountAccessPort, EventAvailabilityPort eventAvailabilityPort,
                                              FavoriteEventsAccessPort favoriteEventsAccessPort, ChooseNotificationChannelApplicationService channelService,
                                              NotificationContentFactory contentFactory, NotificationPolicy policy, NotificationSender sender,
                                              NotificationRepository notificationRepository, DomainEventPublisher publisher) {
        this.accountAccessPort=accountAccessPort; this.eventAvailabilityPort=eventAvailabilityPort; this.favoriteEventsAccessPort=favoriteEventsAccessPort; this.channelService=channelService; this.contentFactory=contentFactory; this.policy=policy; this.sender=sender; this.notificationRepository=notificationRepository; this.publisher=publisher;
    }
    @Transactional
    public NotificationDto send(SendNotificationCommand command) {
        var userId = accountAccessPort.findAccountIdByAccessToken(command.accessToken()).orElseThrow(() -> new UnauthorizedException("Zaloguj się"));
        var contact = accountAccessPort.getUserContactData(userId).orElseThrow(() -> new NotFoundException("Brak danych kontaktowych"));
        var event = eventAvailabilityPort.getEventDetails(command.eventId()).orElseThrow(() -> new NotFoundException("Wydarzenie nie istnieje albo nie jest dostępne"));
        policy.checkCanSend(favoriteEventsAccessPort.isFavorite(userId, command.eventId()), true);
        var channel = channelService.choose(command.channel());
        Notification notification = Notification.create(userId, command.eventId(), channel, contentFactory.create(event, channel));
        notification = notificationRepository.save(notification);
        publisher.publish(new NotificationCreatedEvent(notification.id().value(), userId, command.eventId(), channel.name(), Instant.now()));
        try {
            sender.send(notification, event, contact);
            notification.markAsSent();
            publisher.publish(new NotificationSentEvent(notification.id().value(), userId, command.eventId(), channel.name(), Instant.now()));
        } catch (RuntimeException ex) {
            notification.markAsFailed(ex.getMessage());
            publisher.publish(new NotificationFailedEvent(notification.id().value(), userId, command.eventId(), channel.name(), ex.getMessage(), Instant.now()));
        }
        return NotificationMapper.toDto(notificationRepository.save(notification));
    }
}
