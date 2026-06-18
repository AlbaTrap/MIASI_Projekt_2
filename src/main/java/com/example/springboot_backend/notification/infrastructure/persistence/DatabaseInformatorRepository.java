package com.example.springboot_backend.notification.infrastructure.persistence;
import com.example.springboot_backend.notification.domain.model.Informator;
import com.example.springboot_backend.notification.domain.repository.InformatorRepository;
import com.example.springboot_backend.notification.domain.valueobject.InformatorId;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;
@Repository
public class DatabaseInformatorRepository implements InformatorRepository {
    private final SpringDataJpaInformatorRepository repository;
    public DatabaseInformatorRepository(SpringDataJpaInformatorRepository repository) { this.repository=repository; }
    @Override public Informator save(Informator i) { return toDomain(repository.save(toJpa(i))); }
    @Override public List<Informator> findByUserId(UUID userId) { return repository.findByUserId(userId).stream().map(this::toDomain).toList(); }
    private JpaInformatorEntity toJpa(Informator i) { return new JpaInformatorEntity(i.id().value(), i.userId(), i.eventId(), i.notificationId(), i.message(), i.createdAt()); }
    private Informator toDomain(JpaInformatorEntity e) { return new Informator(InformatorId.of(e.getId()), e.getUserId(), e.getEventId(), e.getNotificationId(), e.getMessage(), e.getCreatedAt()); }
}
