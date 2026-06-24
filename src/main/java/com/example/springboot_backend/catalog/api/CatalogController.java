package com.example.springboot_backend.catalog.api;

import com.example.springboot_backend.catalog.application.dto.CatalogEventDto;
import com.example.springboot_backend.catalog.application.service.EventQueryService;
import com.example.springboot_backend.shared.response.ApiResponse;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/catalog")
public class CatalogController {
    private final EventQueryService queryService;
    public CatalogController(EventQueryService queryService) { this.queryService = queryService; }
    @GetMapping("/events") public ApiResponse<List<CatalogEventDto>> published() { return ApiResponse.ok(queryService.getPublishedEvents()); }
    @GetMapping("/events/{id}") public ApiResponse<CatalogEventDto> details(@PathVariable UUID id) { return ApiResponse.ok(queryService.getPublicEvent(id)); }
}
