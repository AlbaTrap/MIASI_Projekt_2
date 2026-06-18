package com.example.springboot_backend.event.application.command;
import java.util.UUID; import java.time.Instant;
public record UpdateEventCommand(UUID eventId, String title, String description, String city, String address, Instant startDate, Instant endDate, String category) { }
