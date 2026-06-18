package com.example.springboot_backend.favorite.application.port;
import java.util.List;
import java.util.UUID;
public interface FavoriteEventsAccessPort {
    boolean isFavorite(UUID userId, UUID eventId);
    List<UUID> findFavoriteEventIds(UUID userId);
}
