package com.example.springboot_backend.catalog.application.dto;

import com.example.springboot_backend.catalog.domain.model.EventCategory;
import com.example.springboot_backend.catalog.domain.model.EventStatus;
import com.example.springboot_backend.catalog.domain.valueobject.SourceType;
import java.time.Instant;
import java.util.UUID;

public record CatalogEventDto(UUID id, String title, String description, String placeName, String city, String address,
                              Instant startDate, Instant endDate, EventCategory category, String organizerName,
                              SourceType sourceType, EventStatus status) { }
