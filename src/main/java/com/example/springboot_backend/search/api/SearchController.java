package com.example.springboot_backend.search.api;

import com.example.springboot_backend.search.application.dto.*;
import com.example.springboot_backend.search.application.query.SearchEventsQuery;
import com.example.springboot_backend.search.application.service.*;
import com.example.springboot_backend.shared.response.ApiResponse;
import com.example.springboot_backend.shared.security.AdminAuthorizationService;
import com.example.springboot_backend.shared.util.AuthHeader;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class SearchController {
    private final SearchEventsService searchService;
    private final GetFiltersService filtersService;
    private final UpdateSearchIndexService indexService;
    private final AdminAuthorizationService adminAuthorizationService;
    public SearchController(SearchEventsService searchService, GetFiltersService filtersService, UpdateSearchIndexService indexService, AdminAuthorizationService adminAuthorizationService) {
        this.searchService = searchService; this.filtersService = filtersService; this.indexService = indexService; this.adminAuthorizationService = adminAuthorizationService;
    }
    @GetMapping("/events")
    public ApiResponse<SearchResultsListDto> search(@RequestParam(required=false) String phrase,
                                                    @RequestParam(required=false) String category,
                                                    @RequestParam(required=false) String location,
                                                    @RequestParam(required=false) Instant from,
                                                    @RequestParam(required=false) Instant to,
                                                    @RequestParam(defaultValue="DATE") String sortBy,
                                                    @RequestParam(defaultValue="ASC") String direction,
                                                    @RequestParam(defaultValue="0") int page,
                                                    @RequestParam(defaultValue="20") int size) {
        return ApiResponse.ok(searchService.search(new SearchEventsQuery(phrase, category, location, from, to, sortBy, direction, page, size)));
    }
    @GetMapping("/events/filters") public ApiResponse<FilterOptionsDto> filters() { return ApiResponse.ok(filtersService.getFilters()); }
    @PostMapping("/search-index/rebuild") public ApiResponse<Integer> rebuildIndex(@RequestHeader(value="Authorization", required=false) String auth) { adminAuthorizationService.check(AuthHeader.extractToken(auth)); return ApiResponse.ok("Indeks wyszukiwania przebudowany", indexService.rebuildIndex()); }
    @PostMapping("/search-index/events/{id}") public ApiResponse<Void> addToIndex(@PathVariable UUID id, @RequestHeader(value="Authorization", required=false) String auth) { adminAuthorizationService.check(AuthHeader.extractToken(auth)); indexService.addToIndex(id); return ApiResponse.ok("Wydarzenie dodane do indeksu"); }
    @DeleteMapping("/search-index/events/{id}") public ApiResponse<Void> removeFromIndex(@PathVariable UUID id, @RequestHeader(value="Authorization", required=false) String auth) { adminAuthorizationService.check(AuthHeader.extractToken(auth)); indexService.removeFromIndex(id); return ApiResponse.ok("Wydarzenie usunięte z indeksu"); }
}
