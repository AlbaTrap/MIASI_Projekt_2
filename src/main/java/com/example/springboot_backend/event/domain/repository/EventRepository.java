package com.example.springboot_backend.event.domain.repository;
import com.example.springboot_backend.event.domain.model.Event;
import com.example.springboot_backend.event.domain.valueobject.EventId;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface EventRepository {
    Event save(Event event);
    List<Event> saveAll(List<Event> events);
    Optional<Event> findById(EventId eventId);
    List<Event> findAvailable();
    List<Event> search(String category, String city, Instant from, Instant to);
    void delete(EventId eventId);
}
