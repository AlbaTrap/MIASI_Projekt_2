package com.example.springboot_backend.favorite.application.dto;
import java.time.Instant;
import java.util.UUID;
public record FavoriteEventDto(UUID id, UUID userId, UUID eventId, Instant addedAt) { }
