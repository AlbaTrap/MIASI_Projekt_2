package com.example.springboot_backend.catalog.api;

import com.example.springboot_backend.catalog.api.dto.*;
import com.example.springboot_backend.catalog.application.command.*;
import com.example.springboot_backend.catalog.application.dto.CatalogEventDto;
import com.example.springboot_backend.catalog.application.service.*;
import com.example.springboot_backend.shared.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/events")
public class AdminEventController {
    private final EventAdditionService additionService;
    private final EventUpdateService updateService;
    private final EventPublicationService publicationService;
    public AdminEventController(EventAdditionService additionService, EventUpdateService updateService, EventPublicationService publicationService) {
        this.additionService = additionService; this.updateService = updateService; this.publicationService = publicationService;
    }
    @PostMapping public ApiResponse<CatalogEventDto> add(@Valid @RequestBody EventRequest r) {
        var dto = additionService.addEvent(new AddEventCommand(r.title(), r.description(), r.placeName(), r.city(), r.address(), r.startDate(), r.endDate(), r.category(), r.organizerName(), r.organizerWebsite()));
        return ApiResponse.ok("Wydarzenie dodane jako szkic", dto);
    }
    @PutMapping("/{id}") public ApiResponse<CatalogEventDto> update(@PathVariable UUID id, @Valid @RequestBody EventRequest r) {
        return ApiResponse.ok("Wydarzenie zaktualizowane", updateService.updateEvent(new UpdateEventCommand(id, r.title(), r.description(), r.placeName(), r.city(), r.address(), r.startDate(), r.endDate(), r.category(), r.organizerName(), r.organizerWebsite())));
    }
    @PostMapping("/{id}/publish") public ApiResponse<CatalogEventDto> publish(@PathVariable UUID id) { return ApiResponse.ok(publicationService.publish(new EventIdCommand(id))); }
    @PostMapping("/{id}/hide") public ApiResponse<CatalogEventDto> hide(@PathVariable UUID id) { return ApiResponse.ok(publicationService.hide(new EventIdCommand(id))); }
    @PostMapping("/{id}/archive") public ApiResponse<CatalogEventDto> archive(@PathVariable UUID id) { return ApiResponse.ok(publicationService.archive(new EventIdCommand(id))); }
    @PostMapping("/{id}/cancel") public ApiResponse<CatalogEventDto> cancel(@PathVariable UUID id, @RequestBody(required = false) CancelEventRequest r) { return ApiResponse.ok(updateService.cancelEvent(new CancelEventCommand(id, r == null ? "" : r.reason()))); }
}
