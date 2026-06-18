package com.example.springboot_backend.notification.domain.model;
import com.example.springboot_backend.notification.domain.valueobject.InformatorId;
import java.time.Instant;
import java.util.UUID;
public class Informator {
    private final InformatorId id;
    private final UUID userId;
    private final UUID eventId;
    private final UUID notificationId;
    private final String message;
    private final Instant createdAt;
    public Informator(InformatorId id, UUID userId, UUID eventId, UUID notificationId, String message, Instant createdAt) {
        this.id=id; this.userId=userId; this.eventId=eventId; this.notificationId=notificationId; this.message=message; this.createdAt=createdAt;
    }
    public static Informator prepare(UUID userId, UUID eventId, UUID notificationId, String eventTitle) {
        return new Informator(InformatorId.newId(), userId, eventId, notificationId, "Powiadomienie o wydarzeniu '" + eventTitle + "' zostało przygotowane/wysłane.", Instant.now());
    }
    public InformatorId id(){return id;} public UUID userId(){return userId;} public UUID eventId(){return eventId;} public UUID notificationId(){return notificationId;} public String message(){return message;} public Instant createdAt(){return createdAt;}
}
