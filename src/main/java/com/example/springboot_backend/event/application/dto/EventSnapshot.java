package com.example.springboot_backend.event.application.dto;
import java.time.Instant;
import java.util.UUID;
public record EventSnapshot(UUID eventId, String title, Instant startDate, String city, String address, String category) { }
