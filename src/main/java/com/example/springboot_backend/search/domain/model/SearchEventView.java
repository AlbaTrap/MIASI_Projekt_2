package com.example.springboot_backend.search.domain.model;

import java.time.Instant;
import java.util.UUID;

public class SearchEventView {
    private final UUID eventId;
    private final String title;
    private final String shortDescription;
    private final Instant startDate;
    private final String city;
    private final String location;
    private final String category;
    private final String status;

    public SearchEventView(UUID eventId, String title, String shortDescription, Instant startDate, String city, String location, String category, String status) {
        this.eventId = eventId; this.title = title; this.shortDescription = shortDescription; this.startDate = startDate; this.city = city; this.location = location; this.category = category; this.status = status;
    }
    public UUID eventId(){return eventId;} public String title(){return title;} public String shortDescription(){return shortDescription;} public Instant startDate(){return startDate;}
    public String city(){return city;} public String location(){return location;} public String category(){return category;} public String status(){return status;}
}
