package com.example.springboot_backend.event.domain.service;
import com.example.springboot_backend.event.domain.valueobject.Location;
import org.springframework.stereotype.Component;
@Component
public class EventLocationPolicy { public boolean accepted(Location location) { return location != null && location.inWroclaw(); } }
