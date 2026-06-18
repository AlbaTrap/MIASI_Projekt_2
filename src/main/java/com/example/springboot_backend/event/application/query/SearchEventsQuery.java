package com.example.springboot_backend.event.application.query;
import java.time.Instant;
public record SearchEventsQuery(String category, String city, Instant from, Instant to) { }
