package com.example.springboot_backend.favorite.api;
import com.example.springboot_backend.favorite.application.command.AddEventToFavoritesCommand;
import com.example.springboot_backend.favorite.application.command.RemoveFavoriteCommand;
import com.example.springboot_backend.favorite.application.dto.FavoriteEventDto;
import com.example.springboot_backend.favorite.application.service.FavoriteEventApplicationService;
import com.example.springboot_backend.shared.response.ApiResponse;
import com.example.springboot_backend.shared.util.AuthHeader;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {
    private final FavoriteEventApplicationService service;
    public FavoriteController(FavoriteEventApplicationService service) { this.service=service; }
    @PostMapping("/{eventId}") public ApiResponse<FavoriteEventDto> add(@PathVariable UUID eventId, @RequestHeader(value="Authorization", required=false) String auth) { return ApiResponse.ok(service.add(new AddEventToFavoritesCommand(AuthHeader.extractToken(auth), eventId))); }
    @DeleteMapping("/{eventId}") public ApiResponse<Void> remove(@PathVariable UUID eventId, @RequestHeader(value="Authorization", required=false) String auth) { service.remove(new RemoveFavoriteCommand(AuthHeader.extractToken(auth), eventId)); return ApiResponse.ok("Usunięto z ulubionych"); }
    @GetMapping public ApiResponse<List<FavoriteEventDto>> mine(@RequestHeader(value="Authorization", required=false) String auth) { return ApiResponse.ok(service.mine(AuthHeader.extractToken(auth))); }
}
