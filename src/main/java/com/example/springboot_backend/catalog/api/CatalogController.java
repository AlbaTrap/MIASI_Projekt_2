package com.example.springboot_backend.catalog.api;

import com.example.springboot_backend.catalog.api.dto.*;
import com.example.springboot_backend.catalog.application.command.*;
import com.example.springboot_backend.catalog.application.dto.CatalogEventDto;
import com.example.springboot_backend.catalog.application.service.*;
import com.example.springboot_backend.shared.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/catalog")
public class CatalogController {
    private final EventAdditionService additionService;
    private final EventUpdateService updateService;
    private final EventPublicationService publicationService;
    private final EventQueryService queryService;
    public CatalogController(EventAdditionService additionService, EventUpdateService updateService, EventPublicationService publicationService, EventQueryService queryService) {
        this.additionService = additionService;
        this.updateService = updateService;
        this.publicationService = publicationService;
        this.queryService = queryService;
    }
    @GetMapping("/events") public ApiResponse<List<CatalogEventDto>> published() { return ApiResponse.ok(queryService.getPublishedEvents()); }
    @GetMapping("/events/{id}") public ApiResponse<CatalogEventDto> details(@PathVariable UUID id) { return ApiResponse.ok(queryService.getEvent(id)); }
    @PostMapping("/events") public ApiResponse<CatalogEventDto> add(@Valid @RequestBody EventRequest r) {
        return ApiResponse.ok("Wydarzenie dodane do katalogu", additionService.addEvent(new AddEventCommand(r.title(), r.description(), r.placeName(), r.city(), r.address(), r.startDate(), r.endDate(), r.category(), r.organizerName(), r.organizerWebsite())));
    }
    @PutMapping("/events/{id}") public ApiResponse<CatalogEventDto> update(@PathVariable UUID id, @Valid @RequestBody EventRequest r) {
        return ApiResponse.ok("Wydarzenie zaktualizowane", updateService.updateEvent(new UpdateEventCommand(id, r.title(), r.description(), r.placeName(), r.city(), r.address(), r.startDate(), r.endDate(), r.category(), r.organizerName(), r.organizerWebsite())));
    }
    @PostMapping("/events/{id}/publish") public ApiResponse<CatalogEventDto> publish(@PathVariable UUID id) { return ApiResponse.ok("Wydarzenie opublikowane", publicationService.publish(new EventIdCommand(id))); }
    @PostMapping("/events/{id}/hide") public ApiResponse<CatalogEventDto> hide(@PathVariable UUID id) { return ApiResponse.ok("Wydarzenie ukryte", publicationService.hide(new EventIdCommand(id))); }
    @PostMapping("/events/{id}/archive") public ApiResponse<CatalogEventDto> archive(@PathVariable UUID id) { return ApiResponse.ok("Wydarzenie zarchiwizowane", publicationService.archive(new EventIdCommand(id))); }
    @PostMapping("/events/{id}/cancel") public ApiResponse<CatalogEventDto> cancel(@PathVariable UUID id, @RequestBody(required = false) CancelEventRequest r) {
        return ApiResponse.ok("Wydarzenie anulowane", updateService.cancelEvent(new CancelEventCommand(id, r == null ? "" : r.reason())));
    }
}
