package com.example.springboot_backend.importevents.api;

import com.example.springboot_backend.importevents.application.service.FetchEventsService;
import com.example.springboot_backend.importevents.application.service.NormalizeImportedEventsService;
import com.example.springboot_backend.shared.response.ApiResponse;
import com.example.springboot_backend.shared.security.AdminAuthorizationService;
import com.example.springboot_backend.shared.util.AuthHeader;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/import/events")
public class ImportEventsController {
    private final FetchEventsService fetchService;
    private final NormalizeImportedEventsService normalizeService;
    private final AdminAuthorizationService adminAuthorizationService;
    public ImportEventsController(FetchEventsService fetchService, NormalizeImportedEventsService normalizeService, AdminAuthorizationService adminAuthorizationService) {
        this.fetchService = fetchService; this.normalizeService = normalizeService; this.adminAuthorizationService = adminAuthorizationService;
    }
    @PostMapping("/fetch") public ApiResponse<Integer> fetch(@RequestHeader(value="Authorization", required=false) String auth) { adminAuthorizationService.check(AuthHeader.extractToken(auth)); return ApiResponse.ok("Pobrano surowe wydarzenia", fetchService.fetchFromScraper()); }
    @PostMapping("/normalize") public ApiResponse<String> normalize(@RequestHeader(value="Authorization", required=false) String auth) { adminAuthorizationService.check(AuthHeader.extractToken(auth)); return ApiResponse.ok("Normalizacja zakończona", normalizeService.normalize()); }
}
