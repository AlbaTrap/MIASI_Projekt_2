package com.example.springboot_backend.notification.domain.model;
import com.example.springboot_backend.notification.domain.valueobject.*;
import java.time.Instant;
import java.util.UUID;
public class Notification {
    private final NotificationId id;
    private final UUID userId;
    private final UUID eventId;
    private final NotificationChannel channel;
    private final NotificationContent content;
    private final Instant createdAt;
    private Instant sentAt;
    private NotificationStatus status;
    private String failureReason;
    public Notification(NotificationId id, UUID userId, UUID eventId, NotificationChannel channel, NotificationContent content, Instant createdAt, Instant sentAt, NotificationStatus status, String failureReason) {
        this.id=id; this.userId=userId; this.eventId=eventId; this.channel=channel; this.content=content; this.createdAt=createdAt; this.sentAt=sentAt; this.status=status; this.failureReason=failureReason;
    }
    public static Notification create(UUID userId, UUID eventId, NotificationChannel channel, NotificationContent content) { return new Notification(NotificationId.newId(), userId, eventId, channel, content, Instant.now(), null, NotificationStatus.CREATED, null); }
    public void markAsSent() { status=NotificationStatus.SENT; sentAt=Instant.now(); failureReason=null; }
    public void markAsFailed(String reason) { status=NotificationStatus.FAILED; failureReason=reason; }
    public NotificationId id(){return id;} public UUID userId(){return userId;} public UUID eventId(){return eventId;} public NotificationChannel channel(){return channel;} public NotificationContent content(){return content;} public Instant createdAt(){return createdAt;} public Instant sentAt(){return sentAt;} public NotificationStatus status(){return status;} public String failureReason(){return failureReason;}
}
