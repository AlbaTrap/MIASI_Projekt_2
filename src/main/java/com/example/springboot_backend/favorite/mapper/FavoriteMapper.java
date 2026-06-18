package com.example.springboot_backend.favorite.mapper;
import com.example.springboot_backend.favorite.application.dto.FavoriteEventDto;
import com.example.springboot_backend.favorite.domain.model.FavoriteEvent;
public final class FavoriteMapper {
    private FavoriteMapper() {}
    public static FavoriteEventDto toDto(FavoriteEvent f) { return new FavoriteEventDto(f.id().value(), f.userId(), f.eventId(), f.addedAt()); }
}
