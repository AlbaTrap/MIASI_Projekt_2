package com.example.springboot_backend.event.api.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
public record EventRequest(@NotBlank String title, String description, @NotBlank String city, String address,
                           @NotNull Instant startDate, Instant endDate, String category) { }
