package com.example.springboot_backend.favorite.domain.valueobject;
import java.util.UUID;
public record FavoriteEventId(UUID value) {
    public static FavoriteEventId newId() { return new FavoriteEventId(UUID.randomUUID()); }
    public static FavoriteEventId of(UUID value) { return new FavoriteEventId(value); }
}
