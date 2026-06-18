package com.example.springboot_backend.favorite.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
interface SpringDataJpaFavoriteEventRepository extends JpaRepository<JpaFavoriteEventEntity, UUID> {
    List<JpaFavoriteEventEntity> findByUserId(UUID userId);
    Optional<JpaFavoriteEventEntity> findByUserIdAndEventId(UUID userId, UUID eventId);
    boolean existsByUserIdAndEventId(UUID userId, UUID eventId);
}
