package com.example.springboot_backend.importevents.domain.service;

import com.example.springboot_backend.catalog.application.command.AddImportedEventCommand;
import com.example.springboot_backend.importevents.domain.model.RawEvent;
import org.springframework.stereotype.Component;
import java.time.Instant;

@Component
public class RawEventNormalizer {
    public AddImportedEventCommand normalize(RawEvent rawEvent) {
        String[] location = splitLocation(rawEvent.rawLocation());
        Instant startDate = parseDate(rawEvent.rawDate());
        return new AddImportedEventCommand(rawEvent.rawTitle(), rawEvent.rawDescription(), location[1], location[0], location[2],
                startDate, null, rawEvent.rawCategory(), organizerName(rawEvent), rawEvent.source().name(), true);
    }
    private String[] splitLocation(String rawLocation) {
        if (rawLocation == null || rawLocation.isBlank()) return new String[] {"Wrocław", "Brak nazwy miejsca", "Brak adresu"};
        String[] parts = rawLocation.split(",", 3);
        if (parts.length == 1) return new String[] {"Wrocław", parts[0].trim(), parts[0].trim()};
        if (parts.length == 2) return new String[] {parts[0].trim(), parts[1].trim(), parts[1].trim()};
        return new String[] {parts[0].trim(), parts[1].trim(), parts[2].trim()};
    }
    private String organizerName(RawEvent rawEvent) {
        return switch (rawEvent.rawTitle()) {
            case "Koncert jazzowy nad Odrą" -> "Wrocławski Kolektyw Jazzowy";
            case "Warsztaty programowania" -> "Koło Naukowe Software Lab";
            case "Mecz siatkówki" -> "Wrocławski Klub Sportowy";
            case "Spektakl komediowy" -> "Scena Komediowa Capitol";
            case "Spacer historyczny po Nadodrzu" -> "Miejskie Spacery Wrocław";
            case "Targi lokalnego designu" -> "Fundacja Lokalny Design";
            case "Poranek jogi w parku" -> "Aktywny Wrocław";
            case "Spotkanie z literaturą" -> "Mediateka Wrocław";
            case "Warsztaty fotografii miejskiej" -> "Wrocławska Szkoła Fotografii";
            case "Koncert muzyki elektronicznej" -> "Stary Klasztor";
            default -> "Organizator wydarzenia";
        };
    }
    private Instant parseDate(String rawDate) {
        try { return Instant.parse(rawDate); } catch (Exception ignored) { return Instant.now().plusSeconds(86400); }
    }
}
