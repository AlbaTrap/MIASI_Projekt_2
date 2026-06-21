package com.example.springboot_backend.importevents.infrastructure.scraper;

import com.example.springboot_backend.importevents.domain.model.ImportSource;
import com.example.springboot_backend.importevents.domain.model.RawEvent;
import com.example.springboot_backend.importevents.domain.port.EventScraper;
import org.springframework.stereotype.Component;
import java.time.Instant;
import java.util.List;

@Component
public class DemoEventScraperAdapter implements EventScraper {
    @Override
    public List<RawEvent> scrapeEvents() {
        return List.of(
                RawEvent.fetched(ImportSource.SCRAPER, "Koncert jazzowy nad Odrą", "Wieczorny koncert jazzowy w centrum Wrocławia", "Wrocław, Narodowe Forum Muzyki", Instant.now().plusSeconds(86400 * 4).toString(), "KONCERT"),
                RawEvent.fetched(ImportSource.SCRAPER, "Warsztaty programowania", "Otwarte warsztaty dla studentów", "Wrocław, Politechnika Wrocławska", Instant.now().plusSeconds(86400 * 7).toString(), "EDUKACJA"),
                RawEvent.fetched(ImportSource.SCRAPER, "Mecz siatkówki", "Sportowe wydarzenie weekendowe", "Wrocław, Hala Orbita", Instant.now().plusSeconds(86400 * 9).toString(), "SPORT"),
                RawEvent.fetched(ImportSource.SCRAPER, "Wydarzenie spoza miasta", "Ten rekord zostanie odrzucony przez katalog", "Poznań, Stary Rynek", Instant.now().plusSeconds(86400 * 5).toString(), "KULTURA")
        );
    }
}
