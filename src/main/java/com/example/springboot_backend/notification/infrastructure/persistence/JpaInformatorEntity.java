package com.example.springboot_backend.notification.infrastructure.persistence;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
@Entity
@Table(name="informators")
public class JpaInformatorEntity {
    @Id private UUID id;
    private UUID userId;
    private UUID eventId;
    private UUID notificationId;
    @Column(length=4000) private String message;
    private Instant createdAt;
    protected JpaInformatorEntity() {}
    public JpaInformatorEntity(UUID id, UUID userId, UUID eventId, UUID notificationId, String message, Instant createdAt) { this.id=id; this.userId=userId; this.eventId=eventId; this.notificationId=notificationId; this.message=message; this.createdAt=createdAt; }
    public UUID getId(){return id;} public UUID getUserId(){return userId;} public UUID getEventId(){return eventId;} public UUID getNotificationId(){return notificationId;} public String getMessage(){return message;} public Instant getCreatedAt(){return createdAt;}
}
