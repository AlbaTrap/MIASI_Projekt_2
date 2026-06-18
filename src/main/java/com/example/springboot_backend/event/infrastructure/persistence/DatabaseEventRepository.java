package com.example.springboot_backend.event.infrastructure.persistence;
import com.example.springboot_backend.event.domain.model.*;
import com.example.springboot_backend.event.domain.repository.EventRepository;
import com.example.springboot_backend.event.domain.valueobject.*;
import org.springframework.stereotype.Repository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public class DatabaseEventRepository implements EventRepository {
    private final SpringDataJpaEventRepository repository;
    public DatabaseEventRepository(SpringDataJpaEventRepository repository) { this.repository = repository; }
    @Override public Event save(Event event) { return toDomain(repository.save(toJpa(event))); }
    @Override public List<Event> saveAll(List<Event> events) { return repository.saveAll(events.stream().map(this::toJpa).toList()).stream().map(this::toDomain).toList(); }
    @Override public Optional<Event> findById(EventId eventId) { return repository.findById(eventId.value()).map(this::toDomain); }
    @Override public List<Event> findAvailable() { return repository.findByStatus(EventStatus.AVAILABLE).stream().map(this::toDomain).toList(); }
    @Override public List<Event> search(String category, String city, Instant from, Instant to) {
        return repository.findByStatus(EventStatus.AVAILABLE).stream().map(this::toDomain)
                .filter(e -> category == null || category.isBlank() || e.category().equalsIgnoreCase(category))
                .filter(e -> city == null || city.isBlank() || e.location().city().equalsIgnoreCase(city))
                .filter(e -> from == null || !e.date().startDate().isBefore(from))
                .filter(e -> to == null || !e.date().startDate().isAfter(to))
                .toList();
    }
    @Override public void delete(EventId eventId) { repository.deleteById(eventId.value()); }
    private JpaEventEntity toJpa(Event e) { return new JpaEventEntity(e.id().value(), e.title().value(), e.description(), e.location().city(), e.location().address(), e.date().startDate(), e.date().endDate(), e.category(), e.source(), e.status(), e.rejectionReason(), e.createdAt()); }
    private Event toDomain(JpaEventEntity e) { return new Event(EventId.of(e.getId()), new EventTitle(e.getTitle()), e.getDescription(), new Location(e.getCity(), e.getAddress()), new EventDate(e.getStartDate(), e.getEndDate()), e.getCategory(), e.getSource(), e.getStatus(), e.getRejectionReason(), e.getCreatedAt()); }
}
