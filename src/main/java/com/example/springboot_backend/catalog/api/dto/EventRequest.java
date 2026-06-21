package com.example.springboot_backend.catalog.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;

public record EventRequest(@NotBlank String title, String description, String placeName,
                           @NotBlank String city, @NotBlank String address,
                           @NotNull Instant startDate, Instant endDate,
                           String category, String organizerName, String organizerWebsite) { }
