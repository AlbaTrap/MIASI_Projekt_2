package com.example.springboot_backend.catalog.application.command;

import java.time.Instant;

public record AddEventCommand(String title, String description, String placeName, String city, String address,
                              Instant startDate, Instant endDate, String category, String organizerName, String organizerWebsite) { }
