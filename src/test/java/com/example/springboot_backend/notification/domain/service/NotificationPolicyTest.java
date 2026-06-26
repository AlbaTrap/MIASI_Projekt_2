package com.example.springboot_backend.notification.domain.service;

import com.example.springboot_backend.shared.exception.BusinessException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class NotificationPolicyTest {

    private final NotificationPolicy policy = new NotificationPolicy();

    @Test
    void checkCanSend() {
        // 1. Test: Wydarzenie nie jest w ulubionych (pierwszy if)
        assertThatThrownBy(() -> policy.checkCanSend(false, true))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Powiadomienie można wysłać tylko dla wydarzenia dodanego do ulubionych");

        // 2. Test: Brak danych kontaktowych użytkownika (drugi if)
        assertThatThrownBy(() -> policy.checkCanSend(true, false))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Brak danych kontaktowych użytkownika");

        // 3. Test: Scenariusz sukcesu (Oba warunki spełnione)
        assertThatCode(() -> policy.checkCanSend(true, true))
                .doesNotThrowAnyException();
    }
}
