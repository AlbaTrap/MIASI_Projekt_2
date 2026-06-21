package com.example.springboot_backend.catalog.infrastructure.persistence;

import com.example.springboot_backend.catalog.domain.model.EventCategory;
import com.example.springboot_backend.catalog.domain.model.EventStatus;
import com.example.springboot_backend.catalog.domain.valueobject.SourceType;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "catalog_events")
public class JpaCatalogEventEntity {
    @Id private UUID id;
    @Column(nullable = false) private String title;
    @Column(length = 4000) private String description;
    private String placeName;
    private String city;
    private String address;
    private Instant startDate;
    private Instant endDate;
    @Enumerated(EnumType.STRING) private EventCategory category;
    private String organizerName;
    private String organizerWebsite;
    @Enumerated(EnumType.STRING) private SourceType sourceType;
    private String sourceAddress;
    @Enumerated(EnumType.STRING) private EventStatus status;
    private Instant createdAt;
    private Instant updatedAt;
    private String cancelReason;
    protected JpaCatalogEventEntity() { }
    public JpaCatalogEventEntity(UUID id, String title, String description, String placeName, String city, String address, Instant startDate, Instant endDate,
                                 EventCategory category, String organizerName, String organizerWebsite, SourceType sourceType, String sourceAddress,
                                 EventStatus status, Instant createdAt, Instant updatedAt, String cancelReason) {
        this.id = id; this.title = title; this.description = description; this.placeName = placeName; this.city = city; this.address = address;
        this.startDate = startDate; this.endDate = endDate; this.category = category; this.organizerName = organizerName; this.organizerWebsite = organizerWebsite;
        this.sourceType = sourceType; this.sourceAddress = sourceAddress; this.status = status; this.createdAt = createdAt; this.updatedAt = updatedAt; this.cancelReason = cancelReason;
    }
    public UUID getId(){return id;} public String getTitle(){return title;} public String getDescription(){return description;} public String getPlaceName(){return placeName;}
    public String getCity(){return city;} public String getAddress(){return address;} public Instant getStartDate(){return startDate;} public Instant getEndDate(){return endDate;}
    public EventCategory getCategory(){return category;} public String getOrganizerName(){return organizerName;} public String getOrganizerWebsite(){return organizerWebsite;}
    public SourceType getSourceType(){return sourceType;} public String getSourceAddress(){return sourceAddress;} public EventStatus getStatus(){return status;}
    public Instant getCreatedAt(){return createdAt;} public Instant getUpdatedAt(){return updatedAt;} public String getCancelReason(){return cancelReason;}
}
