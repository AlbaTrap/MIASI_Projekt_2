package com.example.springboot_backend.notification.application.service;
import com.example.springboot_backend.account.application.port.AccountAccessPort;
import com.example.springboot_backend.event.application.port.EventAvailabilityPort;
import com.example.springboot_backend.notification.application.dto.InformatorDto;
import com.example.springboot_backend.notification.domain.event.InformatorCreatedEvent;
import com.example.springboot_backend.notification.domain.model.Informator;
import com.example.springboot_backend.notification.domain.repository.InformatorRepository;
import com.example.springboot_backend.notification.mapper.NotificationMapper;
import com.example.springboot_backend.shared.event.DomainEventPublisher;
import com.example.springboot_backend.shared.exception.UnauthorizedException;
import com.example.springboot_backend.shared.util.AuthHeader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class CreateInformatorApplicationService {
    private final AccountAccessPort accountAccessPort;
    private final EventAvailabilityPort eventAvailabilityPort;
    private final InformatorRepository informatorRepository;
    private final DomainEventPublisher publisher;
    public CreateInformatorApplicationService(AccountAccessPort accountAccessPort, EventAvailabilityPort eventAvailabilityPort, InformatorRepository informatorRepository, DomainEventPublisher publisher) {
        this.accountAccessPort=accountAccessPort; this.eventAvailabilityPort=eventAvailabilityPort; this.informatorRepository=informatorRepository; this.publisher=publisher;
    }
    @Transactional
    public InformatorDto create(String accessToken, UUID eventId, UUID notificationId) {
        UUID userId = accountAccessPort.findAccountIdByAccessToken(accessToken).orElseThrow(() -> new UnauthorizedException("Zaloguj się"));
        var event = eventAvailabilityPort.getEventDetails(eventId).orElseThrow();
        Informator informator = informatorRepository.save(Informator.prepare(userId, eventId, notificationId, event.title()));
        publisher.publish(new InformatorCreatedEvent(informator.id().value(), userId, eventId, Instant.now()));
        return NotificationMapper.toDto(informator);
    }
    @Transactional(readOnly = true)
    public List<InformatorDto> mine(String accessToken) {
        UUID userId = accountAccessPort.findAccountIdByAccessToken(accessToken).orElseThrow(() -> new UnauthorizedException("Zaloguj się"));
        return informatorRepository.findByUserId(userId).stream().map(NotificationMapper::toDto).toList();
    }
}
