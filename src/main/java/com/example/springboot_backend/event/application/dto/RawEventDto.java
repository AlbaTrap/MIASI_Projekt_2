package com.example.springboot_backend.event.application.dto;
import java.time.Instant;
import java.util.UUID;
public record RawEventDto(UUID id, String source, String title, String location, String date, Instant fetchedAt, boolean processed) { }
