package com.example.springboot_backend.event.domain.port;
import com.example.springboot_backend.event.domain.model.RawEvent;
import java.util.List;
public interface EventScraper { List<RawEvent> scrapeEvents(); }
