package com.example.springboot_backend.favorite.application.command;
import java.util.UUID;
public record AddEventToFavoritesCommand(String accessToken, UUID eventId) { }
