package com.example.springboot_backend.event.infrastructure.persistence;
import com.example.springboot_backend.event.domain.model.RawEvent;
import com.example.springboot_backend.event.domain.repository.RawEventRepository;
import com.example.springboot_backend.event.domain.valueobject.RawEventId;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public class DatabaseRawEventRepository implements RawEventRepository {
    private final SpringDataJpaRawEventRepository repository;
    public DatabaseRawEventRepository(SpringDataJpaRawEventRepository repository) { this.repository = repository; }
    @Override public RawEvent save(RawEvent rawEvent) { return toDomain(repository.save(toJpa(rawEvent))); }
    @Override public List<RawEvent> saveAll(List<RawEvent> rawEvents) { return repository.saveAll(rawEvents.stream().map(this::toJpa).toList()).stream().map(this::toDomain).toList(); }
    @Override public List<RawEvent> findUnprocessed() { return repository.findByProcessedFalse().stream().map(this::toDomain).toList(); }
    private JpaRawEventEntity toJpa(RawEvent r) { return new JpaRawEventEntity(r.id().value(), r.source(), r.rawTitle(), r.rawDescription(), r.rawLocation(), r.rawDate(), r.rawCategory(), r.fetchedAt(), r.processed()); }
    private RawEvent toDomain(JpaRawEventEntity e) { return new RawEvent(RawEventId.of(e.getId()), e.getSource(), e.getRawTitle(), e.getRawDescription(), e.getRawLocation(), e.getRawDate(), e.getRawCategory(), e.getFetchedAt(), e.isProcessed()); }
}
