package com.example.springboot_backend.event.api;
import com.example.springboot_backend.account.application.port.AccountAccessPort;
import com.example.springboot_backend.event.api.dto.EventRequest;
import com.example.springboot_backend.event.application.command.*;
import com.example.springboot_backend.event.application.dto.EventDto;
import com.example.springboot_backend.event.application.query.SearchEventsQuery;
import com.example.springboot_backend.event.application.service.*;
import com.example.springboot_backend.shared.exception.UnauthorizedException;
import com.example.springboot_backend.shared.response.ApiResponse;
import com.example.springboot_backend.shared.util.AuthHeader;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class EventController {
    private final EventCatalogApplicationService catalogService;
    private final SearchEventsApplicationService searchService;
    private final FetchEventsApplicationService fetchService;
    private final NormalizeEventsApplicationService normalizeService;
    private final SelectEventApplicationService selectService;
    private final AccountAccessPort accountAccessPort;
    public EventController(EventCatalogApplicationService catalogService, SearchEventsApplicationService searchService,
                           FetchEventsApplicationService fetchService, NormalizeEventsApplicationService normalizeService,
                           SelectEventApplicationService selectService, AccountAccessPort accountAccessPort) {
        this.catalogService=catalogService; this.searchService=searchService; this.fetchService=fetchService; this.normalizeService=normalizeService; this.selectService=selectService; this.accountAccessPort=accountAccessPort;
    }
    @GetMapping("/events")
    public ApiResponse<List<EventDto>> search(@RequestParam(required=false) String category, @RequestParam(required=false) String city,
                                              @RequestParam(required=false) Instant from, @RequestParam(required=false) Instant to) {
        return ApiResponse.ok(searchService.search(new SearchEventsQuery(category, city, from, to)));
    }
    @GetMapping("/events/{id}") public ApiResponse<EventDto> details(@PathVariable UUID id) { return ApiResponse.ok(catalogService.details(id)); }
    @PostMapping("/events/{id}/select")
    public ApiResponse<EventDto> select(@PathVariable UUID id, @RequestHeader(value="Authorization", required=false) String auth) {
        UUID userId = accountAccessPort.findAccountIdByAccessToken(AuthHeader.extractToken(auth)).orElseThrow(() -> new UnauthorizedException("Zaloguj się, aby wybrać wydarzenie"));
        return ApiResponse.ok(selectService.select(new SelectEventCommand(id, userId)));
    }
    @PostMapping("/admin/events") public ApiResponse<EventDto> create(@Valid @RequestBody EventRequest r) { return ApiResponse.ok(catalogService.create(new CreateEventCommand(r.title(), r.description(), r.city(), r.address(), r.startDate(), r.endDate(), r.category()))); }
    @PutMapping("/admin/events/{id}") public ApiResponse<EventDto> update(@PathVariable UUID id, @Valid @RequestBody EventRequest r) { return ApiResponse.ok(catalogService.update(new UpdateEventCommand(id, r.title(), r.description(), r.city(), r.address(), r.startDate(), r.endDate(), r.category()))); }
    @DeleteMapping("/admin/events/{id}") public ApiResponse<Void> delete(@PathVariable UUID id) { catalogService.delete(new DeleteEventCommand(id)); return ApiResponse.ok("Wydarzenie usunięte"); }
    @PostMapping("/admin/events/fetch") public ApiResponse<Integer> fetch() { return ApiResponse.ok("Pobrano surowe wydarzenia", fetchService.fetchFromScraper()); }
    @PostMapping("/admin/events/normalize")
    public ApiResponse<String> normalize() {
        return ApiResponse.ok("Normalizacja zakończona", normalizeService.normalize());
    }
}
