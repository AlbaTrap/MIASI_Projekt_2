package com.example.springboot_backend.importevents.infrastructure.scraper;

import com.example.springboot_backend.importevents.domain.model.ImportSource;
import com.example.springboot_backend.importevents.domain.model.RawEvent;
import com.example.springboot_backend.importevents.domain.port.EventScraper;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component("importEventsDemoEventScraperAdapter")
public class DemoEventScraperAdapter implements EventScraper {
    @Override
    public List<RawEvent> scrapeEvents() {
        return List.of(
                RawEvent.fetched(ImportSource.SCRAPER, "Koncert jazzowy nad Odrą", "Wieczór z kwartetem jazzowym, krótką prelekcją o scenie improwizowanej i spokojną atmosferą w centrum miasta.", "Wroclaw, Narodowe Forum Muzyki, plac Wolności 1", Instant.parse("2026-06-27T17:30:00Z").toString(), "KONCERT"),
                RawEvent.fetched(ImportSource.SCRAPER, "Warsztaty programowania", "Praktyczne spotkanie dla studentów i początkujących programistów. Uczestnicy przejdą przez podstawy pracy z API i prostą aplikację webową.", "Wroclaw, Politechnika Wrocławska, wybrzeże Wyspiańskiego 27", Instant.parse("2026-06-28T09:15:00Z").toString(), "EDUKACJA"),
                RawEvent.fetched(ImportSource.SCRAPER, "Mecz siatkówki", "Weekendowe wydarzenie sportowe dla kibiców. W programie mecz główny, strefa rodzinna i krótkie aktywności przed rozpoczęciem spotkania.", "Wroclaw, Hala Orbita, ul. Wejherowska 34", Instant.parse("2026-06-28T15:00:00Z").toString(), "SPORT"),
                RawEvent.fetched(ImportSource.SCRAPER, "Spektakl komediowy", "Lekki spektakl teatralny oparty na miejskich historiach i codziennych absurdach. Dobry wybór na wieczorne wyjście ze znajomymi.", "Wroclaw, Teatr Capitol, ul. Piłsudskiego 67", Instant.parse("2026-06-29T18:45:00Z").toString(), "TEATR"),
                RawEvent.fetched(ImportSource.SCRAPER, "Spacer historyczny po Nadodrzu", "Zwiedzanie dzielnicy z przewodnikiem, archiwalnymi zdjęciami i opowieściami o przemianach Nadodrza w ostatnich dekadach.", "Wroclaw, Plac Staszica, plac Staszica 50", Instant.parse("2026-06-30T08:30:00Z").toString(), "KULTURA"),
                RawEvent.fetched(ImportSource.SCRAPER, "Targi lokalnego designu", "Spotkanie twórców, projektantów i małych marek z Wrocławia. Na miejscu stoiska, rozmowy z autorami i krótki panel o odpowiedzialnym projektowaniu.", "Wroclaw, Czasoprzestrzeń, ul. Tramwajowa 1-3", Instant.parse("2026-07-01T11:00:00Z").toString(), "KULTURA"),
                RawEvent.fetched(ImportSource.SCRAPER, "Poranek jogi w parku", "Otwarte zajęcia ruchowe dla osób na każdym poziomie. Organizator zapewnia spokojne tempo, krótką rozgrzewkę i ćwiczenia oddechowe.", "Wroclaw, Park Szczytnicki, ul. Mickiewicza 1", Instant.parse("2026-07-02T06:45:00Z").toString(), "SPORT"),
                RawEvent.fetched(ImportSource.SCRAPER, "Spotkanie z literaturą", "Rozmowa autorska połączona z dyskusją z czytelnikami. Tematem przewodnim będzie miasto jako bohater współczesnych opowieści.", "Wroclaw, Mediateka, plac Teatralny 5", Instant.parse("2026-07-03T16:20:00Z").toString(), "KULTURA"),
                RawEvent.fetched(ImportSource.SCRAPER, "Warsztaty fotografii miejskiej", "Praktyczne zajęcia z fotografowania architektury i ulicy. Uczestnicy poznają podstawy kadrowania, światła i pracy w przestrzeni miejskiej.", "Wroclaw, Rynek, Rynek 1", Instant.parse("2026-07-04T13:10:00Z").toString(), "EDUKACJA"),
                RawEvent.fetched(ImportSource.SCRAPER, "Koncert muzyki elektronicznej", "Wieczór z lokalnymi artystami, setami live i muzyką klubową. Wydarzenie nastawione na kameralną atmosferę i prezentację młodej sceny.", "Wroclaw, Stary Klasztor, ul. Purkyniego 1", Instant.parse("2026-07-05T20:30:00Z").toString(), "KONCERT"),
                RawEvent.fetched(ImportSource.SCRAPER, "Wydarzenie spoza miasta", "Ten rekord zostanie odrzucony przez katalog", "Poznan, Stary Rynek, Stary Rynek 1", Instant.parse("2026-07-01T10:00:00Z").toString(), "KULTURA")
        );
    }
}
