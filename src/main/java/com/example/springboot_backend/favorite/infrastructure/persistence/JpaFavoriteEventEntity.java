package com.example.springboot_backend.favorite.infrastructure.persistence;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
@Entity
@Table(name="favorite_events", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "event_id"}))
public class JpaFavoriteEventEntity {
    @Id private UUID id;
    @Column(name="user_id", nullable=false) private UUID userId;
    @Column(name="event_id", nullable=false) private UUID eventId;
    @Column(nullable=false) private Instant addedAt;
    protected JpaFavoriteEventEntity() {}
    public JpaFavoriteEventEntity(UUID id, UUID userId, UUID eventId, Instant addedAt) { this.id=id; this.userId=userId; this.eventId=eventId; this.addedAt=addedAt; }
    public UUID getId(){return id;} public UUID getUserId(){return userId;} public UUID getEventId(){return eventId;} public Instant getAddedAt(){return addedAt;}
}
