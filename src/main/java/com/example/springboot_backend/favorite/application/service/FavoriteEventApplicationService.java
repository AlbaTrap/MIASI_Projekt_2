package com.example.springboot_backend.favorite.application.service;
import com.example.springboot_backend.account.application.port.AccountAccessPort;
import com.example.springboot_backend.event.application.port.EventAvailabilityPort;
import com.example.springboot_backend.favorite.application.command.*;
import com.example.springboot_backend.favorite.application.dto.FavoriteEventDto;
import com.example.springboot_backend.favorite.application.port.FavoriteEventsAccessPort;
import com.example.springboot_backend.favorite.domain.event.EventAddedToFavoritesEvent;
import com.example.springboot_backend.favorite.domain.event.FavoriteEventRemovedEvent;
import com.example.springboot_backend.favorite.domain.model.FavoriteEvent;
import com.example.springboot_backend.favorite.domain.repository.FavoriteEventRepository;
import com.example.springboot_backend.favorite.domain.service.FavoriteEventPolicy;
import com.example.springboot_backend.favorite.mapper.FavoriteMapper;
import com.example.springboot_backend.shared.event.DomainEventPublisher;
import com.example.springboot_backend.shared.exception.NotFoundException;
import com.example.springboot_backend.shared.exception.UnauthorizedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class FavoriteEventApplicationService implements FavoriteEventsAccessPort {
    private final FavoriteEventRepository favoriteRepository;
    private final AccountAccessPort accountAccessPort;
    private final EventAvailabilityPort eventAvailabilityPort;
    private final FavoriteEventPolicy policy;
    private final DomainEventPublisher publisher;
    public FavoriteEventApplicationService(FavoriteEventRepository favoriteRepository, AccountAccessPort accountAccessPort,
                                           EventAvailabilityPort eventAvailabilityPort, FavoriteEventPolicy policy, DomainEventPublisher publisher) {
        this.favoriteRepository=favoriteRepository; this.accountAccessPort=accountAccessPort; this.eventAvailabilityPort=eventAvailabilityPort; this.policy=policy; this.publisher=publisher;
    }
    @Transactional
    public FavoriteEventDto add(AddEventToFavoritesCommand command) {
        UUID userId = accountAccessPort.findAccountIdByAccessToken(command.accessToken()).orElseThrow(() -> new UnauthorizedException("Zaloguj się"));
        boolean exists = favoriteRepository.existsByUserIdAndEventId(userId, command.eventId());
        policy.checkCanAdd(userId, command.eventId(), true, eventAvailabilityPort.isEventAvailable(command.eventId()), exists);
        FavoriteEvent saved = favoriteRepository.save(FavoriteEvent.create(userId, command.eventId()));
        publisher.publish(new EventAddedToFavoritesEvent(userId, command.eventId(), Instant.now()));
        return FavoriteMapper.toDto(saved);
    }
    @Transactional
    public void remove(RemoveFavoriteCommand command) {
        UUID userId = accountAccessPort.findAccountIdByAccessToken(command.accessToken()).orElseThrow(() -> new UnauthorizedException("Zaloguj się"));
        FavoriteEvent favorite = favoriteRepository.findByUserIdAndEventId(userId, command.eventId()).orElseThrow(() -> new NotFoundException("Nie znaleziono ulubionego wydarzenia"));
        favoriteRepository.delete(favorite.id());
        publisher.publish(new FavoriteEventRemovedEvent(userId, command.eventId(), Instant.now()));
    }
    @Transactional(readOnly = true)
    public List<FavoriteEventDto> mine(String accessToken) {
        UUID userId = accountAccessPort.findAccountIdByAccessToken(accessToken).orElseThrow(() -> new UnauthorizedException("Zaloguj się"));
        return favoriteRepository.findByUserId(userId).stream().map(FavoriteMapper::toDto).toList();
    }
    @Override
    @Transactional(readOnly = true)
    public boolean isFavorite(UUID userId, UUID eventId) { return favoriteRepository.existsByUserIdAndEventId(userId, eventId); }
    @Override
    @Transactional(readOnly = true)
    public List<UUID> findFavoriteEventIds(UUID userId) { return favoriteRepository.findByUserId(userId).stream().map(FavoriteEvent::eventId).toList(); }
}
