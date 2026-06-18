package com.example.springboot_backend.event.domain.service;
import com.example.springboot_backend.event.domain.model.Event;
import com.example.springboot_backend.shared.exception.BusinessException;
import org.springframework.stereotype.Component;
@Component
public class EventSelectionPolicy {
    private final EventAvailabilityPolicy availabilityPolicy;
    public EventSelectionPolicy(EventAvailabilityPolicy availabilityPolicy) { this.availabilityPolicy = availabilityPolicy; }
    public void check(Event event) { if (!availabilityPolicy.canBeShown(event)) throw new BusinessException("Wybrać można tylko dostępne wydarzenie"); }
}
