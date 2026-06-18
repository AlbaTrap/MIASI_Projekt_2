package com.example.springboot_backend.event.application.dto;
import com.example.springboot_backend.event.domain.model.EventStatus;
import java.time.Instant;
import java.util.UUID;
public record EventDto(UUID id, String title, String description, String city, String address,
                       Instant startDate, Instant endDate, String category, EventStatus status) { }
