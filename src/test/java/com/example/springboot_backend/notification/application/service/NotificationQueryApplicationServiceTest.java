package com.example.springboot_backend.notification.application.service;

import com.example.springboot_backend.account.application.port.AccountAccessPort;
import com.example.springboot_backend.notification.application.dto.NotificationDto;
import com.example.springboot_backend.notification.domain.model.Notification;
import com.example.springboot_backend.notification.domain.repository.NotificationRepository;
import com.example.springboot_backend.shared.exception.UnauthorizedException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationQueryApplicationServiceTest {

    @Mock
    private AccountAccessPort accountAccessPort;

    @Mock
    private NotificationRepository repository;

    @InjectMocks
    private NotificationQueryApplicationService service;

    @Test
    void mine() {
        // given
        String token = "valid-token";
        UUID userId = UUID.randomUUID();

        // Zakładam istnienie klasy domenowej Notification (w paczce com.example.springboot_backend.notification.domain.model)
        Notification mockNotification = mock(Notification.class);

        when(accountAccessPort.findAccountIdByAccessToken(token)).thenReturn(Optional.of(userId));
        when(repository.findByUserId(userId)).thenReturn(List.of(mockNotification));

        // when
        List<NotificationDto> result = service.mine(token);

        // then
        // 1. Weryfikacja scenariusza sukcesu
        assertThat(result).isNotNull().hasSize(1);
        verify(accountAccessPort).findAccountIdByAccessToken(token);
        verify(repository).findByUserId(userId);

        // ---------------------------------------------------------------------
        // 2. Scenariusz błędu: Brak zalogowanego użytkownika (niepoprawny token)
        // ---------------------------------------------------------------------
        // given
        when(accountAccessPort.findAccountIdByAccessToken(token)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> service.mine(token))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("Zaloguj się");

        // Weryfikacja, że przy braku autoryzacji repozytorium nie zostało odpytane
        verifyNoMoreInteractions(repository);
    }
}
