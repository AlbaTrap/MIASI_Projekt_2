package com.example.springboot_backend.event.infrastructure.persistence;
import com.example.springboot_backend.event.domain.model.EventSource;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
@Entity
@Table(name = "raw_events")
public class JpaRawEventEntity {
    @Id private UUID id;
    @Enumerated(EnumType.STRING) private EventSource source;
    private String rawTitle;
    @Column(length = 4000) private String rawDescription;
    private String rawLocation;
    private String rawDate;
    private String rawCategory;
    private Instant fetchedAt;
    private boolean processed;
    protected JpaRawEventEntity() {}
    public JpaRawEventEntity(UUID id, EventSource source, String rawTitle, String rawDescription, String rawLocation, String rawDate, String rawCategory, Instant fetchedAt, boolean processed) {
        this.id=id; this.source=source; this.rawTitle=rawTitle; this.rawDescription=rawDescription; this.rawLocation=rawLocation; this.rawDate=rawDate; this.rawCategory=rawCategory; this.fetchedAt=fetchedAt; this.processed=processed;
    }
    public UUID getId(){return id;} public EventSource getSource(){return source;} public String getRawTitle(){return rawTitle;} public String getRawDescription(){return rawDescription;} public String getRawLocation(){return rawLocation;} public String getRawDate(){return rawDate;} public String getRawCategory(){return rawCategory;} public Instant getFetchedAt(){return fetchedAt;} public boolean isProcessed(){return processed;}
}
