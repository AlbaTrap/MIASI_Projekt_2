package com.example.springboot_backend.favorite.domain.service;

import com.example.springboot_backend.shared.exception.BusinessException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FavoriteEventPolicyTest {

    private final FavoriteEventPolicy policy = new FavoriteEventPolicy();

    @Test
    void checkCanAdd() {
        UUID validUserId = UUID.randomUUID();
        UUID validEventId = UUID.randomUUID();

        // 1. Test: Brak identyfikatora użytkownika (null)
        assertThatThrownBy(() -> policy.checkCanAdd(null, validEventId, true, true, false))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Użytkownik jest wymagany");

        // 2. Test: Brak identyfikatora wydarzenia (null)
        assertThatThrownBy(() -> policy.checkCanAdd(validUserId, null, true, true, false))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Wydarzenie jest wymagane");

        // 3. Test: Użytkownik nie jest zalogowany
        assertThatThrownBy(() -> policy.checkCanAdd(validUserId, validEventId, false, true, false))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Użytkownik musi być zalogowany");

        // 4. Test: Wydarzenie jest niedostępne lub nie istnieje
        assertThatThrownBy(() -> policy.checkCanAdd(validUserId, validEventId, true, false, false))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Wydarzenie nie istnieje albo nie jest dostępne");

        // 5. Test: Wydarzenie znajduje się już w ulubionych
        assertThatThrownBy(() -> policy.checkCanAdd(validUserId, validEventId, true, true, true))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Wydarzenie jest już w ulubionych");

        // 6. Test: Scenariusz sukcesu (Wszystkie warunki poprawne)
        assertThatCode(() -> policy.checkCanAdd(validUserId, validEventId, true, true, false))
                .doesNotThrowAnyException();
    }
}
