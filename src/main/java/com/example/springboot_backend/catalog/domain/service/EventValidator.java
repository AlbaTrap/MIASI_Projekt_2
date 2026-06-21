package com.example.springboot_backend.catalog.domain.service;

import com.example.springboot_backend.catalog.domain.model.CatalogEvent;
import com.example.springboot_backend.shared.exception.BusinessException;
import org.springframework.stereotype.Component;

@Component
public class EventValidator {
    private final WroclawLocationRule locationRule;
    public EventValidator(WroclawLocationRule locationRule) { this.locationRule = locationRule; }
    public void validate(CatalogEvent event) {
        if (event.name() == null) throw new BusinessException("Wydarzenie musi posiadać nazwę");
        if (event.date() == null) throw new BusinessException("Wydarzenie musi posiadać termin rozpoczęcia");
        if (event.location() == null) throw new BusinessException("Wydarzenie musi posiadać lokalizację");
        if (!locationRule.isSatisfied(event.location())) throw new BusinessException("Wydarzenie musi odbywać się we Wrocławiu");
    }
}
