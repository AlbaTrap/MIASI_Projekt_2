package com.example.springboot_backend.notification.infrastructure.persistence;
import com.example.springboot_backend.notification.domain.model.NotificationStatus;
import com.example.springboot_backend.notification.domain.valueobject.NotificationChannel;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
@Entity
@Table(name="notifications")
public class JpaNotificationEntity {
    @Id private UUID id;
    private UUID userId;
    private UUID eventId;
    @Enumerated(EnumType.STRING) private NotificationChannel channel;
    @Enumerated(EnumType.STRING) private NotificationStatus status;
    private String subject;
    @Column(length=4000) private String message;
    private Instant createdAt;
    private Instant sentAt;
    private String failureReason;
    protected JpaNotificationEntity() {}
    public JpaNotificationEntity(UUID id, UUID userId, UUID eventId, NotificationChannel channel, NotificationStatus status, String subject, String message, Instant createdAt, Instant sentAt, String failureReason) {
        this.id=id; this.userId=userId; this.eventId=eventId; this.channel=channel; this.status=status; this.subject=subject; this.message=message; this.createdAt=createdAt; this.sentAt=sentAt; this.failureReason=failureReason;
    }
    public UUID getId(){return id;} public UUID getUserId(){return userId;} public UUID getEventId(){return eventId;} public NotificationChannel getChannel(){return channel;} public NotificationStatus getStatus(){return status;} public String getSubject(){return subject;} public String getMessage(){return message;} public Instant getCreatedAt(){return createdAt;} public Instant getSentAt(){return sentAt;} public String getFailureReason(){return failureReason;}
}
