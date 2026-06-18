package com.example.springboot_backend.notification.domain.valueobject;
import java.util.UUID;
public record NotificationId(UUID value) { public static NotificationId newId(){return new NotificationId(UUID.randomUUID());} public static NotificationId of(UUID value){return new NotificationId(value);} }
