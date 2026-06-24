package com.example.springboot_backend.importevents.infrastructure.persistence;

import com.example.springboot_backend.importevents.domain.model.ImportSource;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity(name = "ImportRawEventEntity")
@Table(name = "import_raw_events")
public class JpaRawEventEntity {
    @Id private UUID id;
    @Enumerated(EnumType.STRING) private ImportSource source;
    private String rawTitle;
    @Column(length = 4000) private String rawDescription;
    private String rawLocation;
    private String rawDate;
    private String rawCategory;
    private Instant fetchedAt;
    private boolean processed;
    protected JpaRawEventEntity() { }
    public JpaRawEventEntity(UUID id, ImportSource source, String rawTitle, String rawDescription, String rawLocation, String rawDate, String rawCategory, Instant fetchedAt, boolean processed) {
        this.id = id; this.source = source; this.rawTitle = rawTitle; this.rawDescription = rawDescription; this.rawLocation = rawLocation; this.rawDate = rawDate; this.rawCategory = rawCategory; this.fetchedAt = fetchedAt; this.processed = processed;
    }
    public UUID getId(){return id;} public ImportSource getSource(){return source;} public String getRawTitle(){return rawTitle;} public String getRawDescription(){return rawDescription;}
    public String getRawLocation(){return rawLocation;} public String getRawDate(){return rawDate;} public String getRawCategory(){return rawCategory;} public Instant getFetchedAt(){return fetchedAt;} public boolean isProcessed(){return processed;}
}
