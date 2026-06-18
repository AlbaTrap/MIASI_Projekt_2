package com.example.springboot_backend.event.domain.service;
import com.example.springboot_backend.event.domain.model.Event;
import com.example.springboot_backend.event.domain.model.EventStatus;
import org.springframework.stereotype.Component;
@Component
public class EventAvailabilityPolicy { public boolean canBeShown(Event event) { return event != null && event.status() == EventStatus.AVAILABLE && event.location().inWroclaw(); } }
