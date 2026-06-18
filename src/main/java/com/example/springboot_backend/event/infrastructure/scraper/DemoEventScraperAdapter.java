package com.example.springboot_backend.event.infrastructure.scraper;
import com.example.springboot_backend.event.domain.model.EventSource;
import com.example.springboot_backend.event.domain.model.RawEvent;
import com.example.springboot_backend.event.domain.port.EventScraper;
import org.springframework.stereotype.Component;
import java.time.Instant;
import java.util.List;
@Component
public class DemoEventScraperAdapter implements EventScraper {
    @Override public List<RawEvent> scrapeEvents() {
        return List.of(
            RawEvent.fetched(EventSource.SCRAPER, "Koncert jazzowy", "Wieczorny koncert w centrum", "Wrocław, Rynek 1", Instant.now().plusSeconds(86400).toString(), "muzyka"),
            RawEvent.fetched(EventSource.SCRAPER, "Warsztaty Java", "Spotkanie edukacyjne", "Wrocław, ul. Politechniczna 5", Instant.now().plusSeconds(172800).toString(), "edukacja"),
            RawEvent.fetched(EventSource.SCRAPER, "Event spoza miasta", "To wydarzenie powinno zostać odrzucone", "Warszawa, Centrum", Instant.now().plusSeconds(7200).toString(), "inne")
        );
    }
}
