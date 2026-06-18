package com.example.springboot_backend.favorite.infrastructure.persistence;
import com.example.springboot_backend.favorite.domain.model.FavoriteEvent;
import com.example.springboot_backend.favorite.domain.repository.FavoriteEventRepository;
import com.example.springboot_backend.favorite.domain.valueobject.FavoriteEventId;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
public class DatabaseFavoriteEventRepository implements FavoriteEventRepository {
    private final SpringDataJpaFavoriteEventRepository repository;
    public DatabaseFavoriteEventRepository(SpringDataJpaFavoriteEventRepository repository) { this.repository=repository; }
    @Override public FavoriteEvent save(FavoriteEvent favoriteEvent) { return toDomain(repository.save(toJpa(favoriteEvent))); }
    @Override public List<FavoriteEvent> findByUserId(UUID userId) { return repository.findByUserId(userId).stream().map(this::toDomain).toList(); }
    @Override public Optional<FavoriteEvent> findByUserIdAndEventId(UUID userId, UUID eventId) { return repository.findByUserIdAndEventId(userId, eventId).map(this::toDomain); }
    @Override public boolean existsByUserIdAndEventId(UUID userId, UUID eventId) { return repository.existsByUserIdAndEventId(userId, eventId); }
    @Override public void delete(FavoriteEventId id) { repository.deleteById(id.value()); }
    private JpaFavoriteEventEntity toJpa(FavoriteEvent f) { return new JpaFavoriteEventEntity(f.id().value(), f.userId(), f.eventId(), f.addedAt()); }
    private FavoriteEvent toDomain(JpaFavoriteEventEntity e) { return new FavoriteEvent(FavoriteEventId.of(e.getId()), e.getUserId(), e.getEventId(), e.getAddedAt()); }
}
