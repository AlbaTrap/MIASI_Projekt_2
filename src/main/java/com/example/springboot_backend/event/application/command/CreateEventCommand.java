package com.example.springboot_backend.event.application.command;
import java.time.Instant;
public record CreateEventCommand(String title, String description, String city, String address, Instant startDate, Instant endDate, String category) { }
