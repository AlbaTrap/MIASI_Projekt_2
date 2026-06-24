package com.example.springboot_backend.catalog.infrastructure.persistence;

import com.example.springboot_backend.catalog.domain.model.CatalogEvent;
import com.example.springboot_backend.catalog.domain.model.EventStatus;
import com.example.springboot_backend.catalog.domain.repository.CatalogEventRepository;
import com.example.springboot_backend.catalog.domain.valueobject.*;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public class DatabaseCatalogEventRepository implements CatalogEventRepository {
    private final SpringDataJpaCatalogEventRepository repository;
    public DatabaseCatalogEventRepository(SpringDataJpaCatalogEventRepository repository) { this.repository = repository; }
    @Override public CatalogEvent save(CatalogEvent event) { return toDomain(repository.save(toJpa(event))); }
    @Override public List<CatalogEvent> saveAll(List<CatalogEvent> events) { return repository.saveAll(events.stream().map(this::toJpa).toList()).stream().map(this::toDomain).toList(); }
    @Override public Optional<CatalogEvent> findById(CatalogEventId id) { return repository.findById(id.value()).map(this::toDomain); }
    @Override public List<CatalogEvent> findPublished() { return repository.findByStatus(EventStatus.PUBLISHED).stream().map(this::toDomain).toList(); }
    @Override public List<CatalogEvent> findForIndexing() { return repository.findByStatus(EventStatus.PUBLISHED).stream().map(this::toDomain).toList(); }
    @Override public boolean existsById(CatalogEventId id) { return repository.existsById(id.value()); }
    @Override public void delete(CatalogEventId id) { repository.deleteById(id.value()); }
    private JpaCatalogEventEntity toJpa(CatalogEvent e) {
        return new JpaCatalogEventEntity(e.id().value(), e.name().value(), e.description().value(), e.location().placeName(), e.location().city(), e.location().addressText(),
                e.date().start(), e.date().end(), e.category(), e.organizer().name(), e.organizer().website(), e.source().type(), e.source().sourceAddress(), e.status(), e.createdAt(), e.updatedAt(), e.cancelReason());
    }
    private CatalogEvent toDomain(JpaCatalogEventEntity e) {
        return new CatalogEvent(CatalogEventId.of(e.getId()), new EventName(e.getTitle()), new EventDescription(e.getDescription()), new EventDate(e.getStartDate(), e.getEndDate()),
                new Location(e.getPlaceName(), new Address(e.getAddress(), "", e.getCity()), null), e.getCategory(), new Organizer(e.getOrganizerName(), e.getOrganizerWebsite()), new EventSource(e.getSourceType(), e.getSourceAddress()),
                e.getStatus(), e.getCreatedAt(), e.getUpdatedAt(), e.getCancelReason());
    }
}
