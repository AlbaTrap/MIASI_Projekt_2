package com.example.springboot_backend.search.infrastructure.persistence;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "search_event_index")
public class JpaSearchEventViewEntity {
    @Id private UUID eventId;
    private String title;
    @Column(length = 1000) private String shortDescription;
    private Instant startDate;
    private String city;
    private String location;
    private String category;
    private String status;
    protected JpaSearchEventViewEntity() { }
    public JpaSearchEventViewEntity(UUID eventId, String title, String shortDescription, Instant startDate, String city, String location, String category, String status) {
        this.eventId = eventId; this.title = title; this.shortDescription = shortDescription; this.startDate = startDate; this.city = city; this.location = location; this.category = category; this.status = status;
    }
    public UUID getEventId(){return eventId;} public String getTitle(){return title;} public String getShortDescription(){return shortDescription;} public Instant getStartDate(){return startDate;}
    public String getCity(){return city;} public String getLocation(){return location;} public String getCategory(){return category;} public String getStatus(){return status;}
}
