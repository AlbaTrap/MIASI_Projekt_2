package com.example.springboot_backend.event.infrastructure.persistence;
import com.example.springboot_backend.event.domain.model.*;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
@Entity
@Table(name = "events")
public class JpaEventEntity {
    @Id private UUID id;
    @Column(nullable = false) private String title;
    @Column(length = 4000) private String description;
    private String city;
    private String address;
    private Instant startDate;
    private Instant endDate;
    private String category;
    @Enumerated(EnumType.STRING) private EventSource source;
    @Enumerated(EnumType.STRING) private EventStatus status;
    @Enumerated(EnumType.STRING) private RejectionReason rejectionReason;
    private Instant createdAt;
    protected JpaEventEntity() {}
    public JpaEventEntity(UUID id, String title, String description, String city, String address, Instant startDate, Instant endDate,
                          String category, EventSource source, EventStatus status, RejectionReason rejectionReason, Instant createdAt) {
        this.id=id; this.title=title; this.description=description; this.city=city; this.address=address; this.startDate=startDate; this.endDate=endDate;
        this.category=category; this.source=source; this.status=status; this.rejectionReason=rejectionReason; this.createdAt=createdAt;
    }
    public UUID getId(){return id;} public String getTitle(){return title;} public String getDescription(){return description;} public String getCity(){return city;}
    public String getAddress(){return address;} public Instant getStartDate(){return startDate;} public Instant getEndDate(){return endDate;} public String getCategory(){return category;}
    public EventSource getSource(){return source;} public EventStatus getStatus(){return status;} public RejectionReason getRejectionReason(){return rejectionReason;} public Instant getCreatedAt(){return createdAt;}
}
