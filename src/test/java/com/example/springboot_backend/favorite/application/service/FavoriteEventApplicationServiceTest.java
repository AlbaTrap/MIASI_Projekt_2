package com.example.springboot_backend.favorite.application.service;

import com.example.springboot_backend.account.application.port.AccountAccessPort;
import com.example.springboot_backend.catalog.application.dto.CatalogEventDto;
import com.example.springboot_backend.catalog.application.port.EventAvailabilityPort;
import com.example.springboot_backend.favorite.application.command.AddEventToFavoritesCommand;
import com.example.springboot_backend.favorite.application.command.RemoveFavoriteCommand;
import com.example.springboot_backend.favorite.application.dto.FavoriteEventDto;
import com.example.springboot_backend.favorite.domain.event.EventAddedToFavoritesEvent;
import com.example.springboot_backend.favorite.domain.event.FavoriteEventRemovedEvent;
import com.example.springboot_backend.favorite.domain.model.FavoriteEvent;
import com.example.springboot_backend.favorite.domain.repository.FavoriteEventRepository;
import com.example.springboot_backend.favorite.domain.service.FavoriteEventPolicy;
import com.example.springboot_backend.shared.event.DomainEventPublisher;
import com.example.springboot_backend.shared.exception.NotFoundException;
import com.example.springboot_backend.shared.exception.UnauthorizedException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FavoriteEventApplicationServiceTest {

    @Mock
    private FavoriteEventRepository favoriteRepository;

    @Mock
    private AccountAccessPort accountAccessPort;

    @Mock
    private EventAvailabilityPort eventAvailabilityPort;

    @Mock
    private FavoriteEventPolicy policy;

    @Mock
    private DomainEventPublisher publisher;

    @InjectMocks
    private FavoriteEventApplicationService favoriteEventApplicationService;

    @Test
    void add() {
        // given
        String token = "valid-token";
        UUID userId = UUID.randomUUID();
        UUID eventId = UUID.randomUUID();
        AddEventToFavoritesCommand command = new AddEventToFavoritesCommand(token, eventId);

        FavoriteEvent mockFavorite = mock(FavoriteEvent.class);

        when(accountAccessPort.findAccountIdByAccessToken(token)).thenReturn(Optional.of(userId));
        when(favoriteRepository.existsByUserIdAndEventId(userId, eventId)).thenReturn(false);
        when(eventAvailabilityPort.isEventAvailable(eventId)).thenReturn(true);
        when(favoriteRepository.save(any(FavoriteEvent.class))).thenReturn(mockFavorite);

        // when
        FavoriteEventDto result = favoriteEventApplicationService.add(command);

        // then
        verify(policy).checkCanAdd(userId, eventId, true, true, false);
        verify(favoriteRepository).save(any(FavoriteEvent.class));
        verify(publisher).publish(any(EventAddedToFavoritesEvent.class));
        assertThat(result).isNotNull();

        // Scenariusz błędu: Nieautoryzowany użytkownik
        when(accountAccessPort.findAccountIdByAccessToken(token)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> favoriteEventApplicationService.add(command))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("Zaloguj się");
    }

    @Test
    void remove() {
        // given
        String token = "valid-token";
        UUID userId = UUID.randomUUID();
        UUID eventId = UUID.randomUUID();
        UUID favoriteId = UUID.randomUUID();
        RemoveFavoriteCommand command = new RemoveFavoriteCommand(token, eventId);

        FavoriteEvent mockFavorite = mock(FavoriteEvent.class);
        when(mockFavorite.id()).thenReturn(favoriteId);

        when(accountAccessPort.findAccountIdByAccessToken(token)).thenReturn(Optional.of(userId));
        when(favoriteRepository.findByUserIdAndEventId(userId, eventId)).thenReturn(Optional.of(mockFavorite));

        // when
        favoriteEventApplicationService.remove(command);

        // then
        verify(favoriteRepository).delete(favoriteId);
        verify(publisher).publish(any(FavoriteEventRemovedEvent.class));

        // Scenariusz błędu: Brak zasobu w ulubionych
        when(favoriteRepository.findByUserIdAndEventId(userId, eventId)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> favoriteEventApplicationService.remove(command))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Nie znaleziono ulubionego wydarzenia");
    }

    @Test
    void mine() {
        // given
        String token = "valid-token";
        UUID userId = UUID.randomUUID();
        FavoriteEvent mockFavorite = mock(FavoriteEvent.class);

        when(accountAccessPort.findAccountIdByAccessToken(token)).thenReturn(Optional.of(userId));
        when(favoriteRepository.findByUserId(userId)).thenReturn(List.of(mockFavorite));

        // when
        List<FavoriteEventDto> result = favoriteEventApplicationService.mine(token);

        // then
        assertThat(result).hasSize(1);
        verify(favoriteRepository).findByUserId(userId);
    }

    @Test
    void events() {
        // given
        String token = "valid-token";
        UUID userId = UUID.randomUUID();
        UUID eventId = UUID.randomUUID();

        FavoriteEvent mockFavorite = mock(FavoriteEvent.class);
        CatalogEventDto mockCatalogDto = mock(CatalogEventDto.class);

        when(mockFavorite.eventId()).thenReturn(eventId);
        when(accountAccessPort.findAccountIdByAccessToken(token)).thenReturn(Optional.of(userId));
        when(favoriteRepository.findByUserId(userId)).thenReturn(List.of(mockFavorite));
        when(eventAvailabilityPort.getFavoriteEventDetails(eventId)).thenReturn(Optional.of(mockCatalogDto));

        // when
        List<CatalogEventDto> result = favoriteEventApplicationService.events(token);

        // then
        assertThat(result).hasSize(1).contains(mockCatalogDto);
        verify(eventAvailabilityPort).getFavoriteEventDetails(eventId);
    }

    @Test
    void isFavorite() {
        // given
        UUID userId = UUID.randomUUID();
        UUID eventId = UUID.randomUUID();
        when(favoriteRepository.existsByUserIdAndEventId(userId, eventId)).thenReturn(true);

        // when
        boolean result = favoriteEventApplicationService.isFavorite(userId, eventId);

        // then
        assertThat(result).isTrue();
        verify(favoriteRepository).existsByUserIdAndEventId(userId, eventId);
    }

    @Test
    void findFavoriteEventIds() {
        // given
        UUID userId = UUID.randomUUID();
        UUID eventId = UUID.randomUUID();
        FavoriteEvent mockFavorite = mock(FavoriteEvent.class);

        when(mockFavorite.eventId()).thenReturn(eventId);
        when(favoriteRepository.findByUserId(userId)).thenReturn(List.of(mockFavorite));

        // when
        List<UUID> result = favoriteEventApplicationService.findFavoriteEventIds(userId);

        // then
        assertThat(result).hasSize(1).contains(eventId);
        verify(favoriteRepository).findByUserId(userId);
    }
}
