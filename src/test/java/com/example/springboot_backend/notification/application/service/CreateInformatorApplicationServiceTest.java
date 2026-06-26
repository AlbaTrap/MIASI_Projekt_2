package com.example.springboot_backend.notification.application.service;

import com.example.springboot_backend.account.application.port.AccountAccessPort;
import com.example.springboot_backend.catalog.application.dto.CatalogEventDto;
import com.example.springboot_backend.catalog.application.port.EventAvailabilityPort;
import com.example.springboot_backend.notification.application.dto.InformatorDto;
import com.example.springboot_backend.notification.domain.event.InformatorCreatedEvent;
import com.example.springboot_backend.notification.domain.model.Informator;
import com.example.springboot_backend.notification.domain.repository.InformatorRepository;
import com.example.springboot_backend.shared.event.DomainEventPublisher;
import com.example.springboot_backend.shared.exception.UnauthorizedException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateInformatorApplicationServiceTest {

    @Mock
    private AccountAccessPort accountAccessPort;

    @Mock
    private EventAvailabilityPort eventAvailabilityPort;

    @Mock
    private InformatorRepository informatorRepository;

    @Mock
    private DomainEventPublisher publisher;

    @InjectMocks
    private CreateInformatorApplicationService service;

    @Test
    void create() {
        // given
        String token = "valid-token";
        UUID eventId = UUID.randomUUID();
        UUID notificationId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID informatorId = UUID.randomUUID();

        CatalogEventDto mockEventDto = mock(CatalogEventDto.class);
        when(mockEventDto.title()).thenReturn("Koncert muzyki filmowej");

        Informator mockInformator = mock(Informator.class, Mockito.RETURNS_DEEP_STUBS);
        when(mockInformator.id().value()).thenReturn(informatorId);

        when(accountAccessPort.findAccountIdByAccessToken(token)).thenReturn(Optional.of(userId));
        when(eventAvailabilityPort.getEventDetails(eventId)).thenReturn(Optional.of(mockEventDto));
        when(informatorRepository.save(any(Informator.class))).thenReturn(mockInformator);

        // when
        InformatorDto result = service.create(token, eventId, notificationId);

        // then
        // Weryfikacja pełnego, poprawnego przepływu biznesowego
        verify(informatorRepository).save(any(Informator.class));
        verify(publisher).publish(any(InformatorCreatedEvent.class));
        assertThat(result).isNotNull();

        // ---------------------------------------------------------------------
        // Scenariusz błędu 1: Niezalogowany użytkownik (brak konta pod tokenem)
        // ---------------------------------------------------------------------
        when(accountAccessPort.findAccountIdByAccessToken(token)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(token, eventId, notificationId))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("Zaloguj się");

        // ---------------------------------------------------------------------
        // Scenariusz błędu 2: Wydarzenie nie istnieje (wyzwalane przez .orElseThrow())
        // ---------------------------------------------------------------------
        when(accountAccessPort.findAccountIdByAccessToken(token)).thenReturn(Optional.of(userId));
        when(eventAvailabilityPort.getEventDetails(eventId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(token, eventId, notificationId))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void mine() {
        // given
        String token = "valid-token";
        UUID userId = UUID.randomUUID();
        Informator mockInformator = mock(Informator.class);

        when(accountAccessPort.findAccountIdByAccessToken(token)).thenReturn(Optional.of(userId));
        when(informatorRepository.findByUserId(userId)).thenReturn(List.of(mockInformator));

        // when
        List<InformatorDto> result = service.mine(token);

        // then
        assertThat(result).hasSize(1);
        verify(informatorRepository).findByUserId(userId);

        // ---------------------------------------------------------------------
        // Scenariusz błędu: Niezalogowany użytkownik przy próbie pobrania listy
        // ---------------------------------------------------------------------
        when(accountAccessPort.findAccountIdByAccessToken(token)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.mine(token))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("Zaloguj się");
    }
}
