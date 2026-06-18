package com.example.springboot_backend.notification.domain.valueobject;
import java.util.UUID;
public record InformatorId(UUID value) { public static InformatorId newId(){return new InformatorId(UUID.randomUUID());} public static InformatorId of(UUID value){return new InformatorId(value);} }
