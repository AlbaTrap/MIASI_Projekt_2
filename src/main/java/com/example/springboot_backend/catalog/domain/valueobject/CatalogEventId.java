package com.example.springboot_backend.catalog.domain.valueobject;

import java.util.UUID;

public record CatalogEventId(UUID value) {
    public CatalogEventId {
        if (value == null) throw new IllegalArgumentException("Id wydarzenia nie może być puste");
    }
    public static CatalogEventId newId() { return new CatalogEventId(UUID.randomUUID()); }
    public static CatalogEventId of(UUID value) { return new CatalogEventId(value); }
}
