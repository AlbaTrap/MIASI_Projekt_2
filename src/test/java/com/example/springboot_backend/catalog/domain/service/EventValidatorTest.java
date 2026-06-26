package com.example.springboot_backend.catalog.domain.service;

import com.example.springboot_backend.catalog.domain.model.CatalogEvent;
import com.example.springboot_backend.catalog.domain.valueobject.EventDate;
import com.example.springboot_backend.catalog.domain.valueobject.EventName;
import com.example.springboot_backend.catalog.domain.valueobject.Location;
import com.example.springboot_backend.shared.exception.BusinessException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventValidatorTest {

    @Mock
    private WroclawLocationRule locationRule;

    @InjectMocks
    private EventValidator eventValidator;

    @Test
    void validate() {
        // 1. Test: Brak nazwy wydarzenia
        CatalogEvent eventWithoutName = mock(CatalogEvent.class);
        when(eventWithoutName.name()).thenReturn(null);

        assertThatThrownBy(() -> eventValidator.validate(eventWithoutName))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Wydarzenie musi posiadać nazwę");

        // 2. Test: Brak terminu/daty
        CatalogEvent eventWithoutDate = mock(CatalogEvent.class);
        when(eventWithoutDate.name()).thenReturn(mock(EventName.class));
        when(eventWithoutDate.date()).thenReturn(null);

        assertThatThrownBy(() -> eventValidator.validate(eventWithoutDate))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Wydarzenie musi posiadać termin rozpoczęcia");

        // 3. Test: Brak lokalizacji
        CatalogEvent eventWithoutLocation = mock(CatalogEvent.class);
        when(eventWithoutLocation.name()).thenReturn(mock(EventName.class));
        when(eventWithoutLocation.date()).thenReturn(mock(EventDate.class));
        when(eventWithoutLocation.location()).thenReturn(null);

        assertThatThrownBy(() -> eventValidator.validate(eventWithoutLocation))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Wydarzenie musi posiadać lokalizację");

        // 4. Test: Lokalizacja nie spełnia reguły Wrocławia
        CatalogEvent eventOutsideWroclaw = mock(CatalogEvent.class);
        Location foreignLocation = mock(Location.class);

        when(eventOutsideWroclaw.name()).thenReturn(mock(EventName.class));
        when(eventOutsideWroclaw.date()).thenReturn(mock(EventDate.class));
        when(eventOutsideWroclaw.location()).thenReturn(foreignLocation);
        when(locationRule.isSatisfied(foreignLocation)).thenReturn(false);

        assertThatThrownBy(() -> eventValidator.validate(eventOutsideWroclaw))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Wydarzenie musi odbywać się we Wrocławiu");

        // 5. Test: Wszystkie warunki poprawne (scenariusz sukcesu)
        CatalogEvent validEvent = mock(CatalogEvent.class);
        Location wroclawLocation = mock(Location.class);

        when(validEvent.name()).thenReturn(mock(EventName.class));
        when(validEvent.date()).thenReturn(mock(EventDate.class));
        when(validEvent.location()).thenReturn(wroclawLocation);
        when(locationRule.isSatisfied(wroclawLocation)).thenReturn(true);

        assertThatCode(() -> eventValidator.validate(validEvent))
                .doesNotThrowAnyException();
    }
}
