package com.example.springboot_backend.catalog.domain.service;

import com.example.springboot_backend.catalog.domain.valueobject.Location;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class WroclawLocationRuleTest {

    private final WroclawLocationRule wroclawLocationRule = new WroclawLocationRule();

    @Test
    void isSatisfied() {
        // 1. Scenariusz: Lokalizacja znajduje się we Wrocławiu
        Location wroclawLocation = mock(Location.class);
        when(wroclawLocation.isInWroclaw()).thenReturn(true);

        boolean resultSuccess = wroclawLocationRule.isSatisfied(wroclawLocation);
        assertThat(resultSuccess).isTrue();

        // 2. Scenariusz: Lokalizacja znajduje się poza Wrocławiem
        Location otherLocation = mock(Location.class);
        when(otherLocation.isInWroclaw()).thenReturn(false);

        boolean resultFailure = wroclawLocationRule.isSatisfied(otherLocation);
        assertThat(resultFailure).isFalse();

        // 3. Scenariusz: Przekazano obiekt null jako lokalizację
        boolean resultNull = wroclawLocationRule.isSatisfied(null);
        assertThat(resultNull).isFalse();
    }
}
