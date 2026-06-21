package com.example.springboot_backend.catalog.mapper;

import com.example.springboot_backend.catalog.application.dto.CatalogEventDto;
import com.example.springboot_backend.catalog.application.dto.EventSnapshot;
import com.example.springboot_backend.catalog.domain.model.CatalogEvent;

public final class CatalogEventMapper {
    private CatalogEventMapper() { }
    public static CatalogEventDto toDto(CatalogEvent e) {
        return new CatalogEventDto(
                e.id().value(), e.name().value(), e.description().value(), e.location().placeName(), e.location().city(), e.location().addressText(),
                e.date().start(), e.date().end(), e.category(), e.organizer().name(), e.source().type(), e.status()
        );
    }
    public static EventSnapshot toSnapshot(CatalogEvent e) {
        return new EventSnapshot(e.id().value(), e.name().value(), e.date().start(), e.location().city(), e.location().addressText(), e.category().name());
    }
}
