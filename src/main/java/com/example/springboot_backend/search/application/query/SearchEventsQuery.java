package com.example.springboot_backend.search.application.query;

import java.time.Instant;

public record SearchEventsQuery(String phrase, String category, String location, Instant from, Instant to,
                                String sortBy, String direction, int page, int size) { }
