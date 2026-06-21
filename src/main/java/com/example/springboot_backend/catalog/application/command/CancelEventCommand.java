package com.example.springboot_backend.catalog.application.command;

import java.util.UUID;

public record CancelEventCommand(UUID eventId, String reason) { }
