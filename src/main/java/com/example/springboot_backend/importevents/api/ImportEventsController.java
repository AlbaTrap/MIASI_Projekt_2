package com.example.springboot_backend.importevents.api;

import com.example.springboot_backend.importevents.application.service.FetchEventsService;
import com.example.springboot_backend.importevents.application.service.NormalizeImportedEventsService;
import com.example.springboot_backend.shared.response.ApiResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/import/events")
public class ImportEventsController {
    private final FetchEventsService fetchService;
    private final NormalizeImportedEventsService normalizeService;
    public ImportEventsController(FetchEventsService fetchService, NormalizeImportedEventsService normalizeService) {
        this.fetchService = fetchService; this.normalizeService = normalizeService;
    }
    @PostMapping("/fetch") public ApiResponse<Integer> fetch() { return ApiResponse.ok("Pobrano surowe wydarzenia", fetchService.fetchFromScraper()); }
    @PostMapping("/normalize") public ApiResponse<String> normalize() { return ApiResponse.ok("Normalizacja zakończona", normalizeService.normalize()); }
}
