package com.example.springboot_backend.event.application.command;
import java.util.UUID;
public record DeleteEventCommand(UUID eventId) { }
