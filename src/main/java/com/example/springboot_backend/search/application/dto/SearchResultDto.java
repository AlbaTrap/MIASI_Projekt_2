package com.example.springboot_backend.search.application.dto;

import java.time.Instant;
import java.util.UUID;

public record SearchResultDto(UUID eventId, String title, String shortDescription, Instant startDate, String location, String category) { }
