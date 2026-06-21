package com.example.springboot_backend.importevents.domain.port;

import com.example.springboot_backend.importevents.domain.model.RawEvent;
import java.util.List;

public interface EventScraper {
    List<RawEvent> scrapeEvents();
}
