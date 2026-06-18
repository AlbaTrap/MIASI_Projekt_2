package com.example.springboot_backend.favorite.domain.repository;
import com.example.springboot_backend.favorite.domain.model.FavoriteEvent;
import com.example.springboot_backend.favorite.domain.valueobject.FavoriteEventId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
public interface FavoriteEventRepository {
    FavoriteEvent save(FavoriteEvent favoriteEvent);
    List<FavoriteEvent> findByUserId(UUID userId);
    Optional<FavoriteEvent> findByUserIdAndEventId(UUID userId, UUID eventId);
    boolean existsByUserIdAndEventId(UUID userId, UUID eventId);
    void delete(FavoriteEventId id);
}
