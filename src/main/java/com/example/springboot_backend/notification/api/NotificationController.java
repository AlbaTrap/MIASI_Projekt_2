package com.example.springboot_backend.notification.api;
import com.example.springboot_backend.notification.application.command.SendNotificationCommand;
import com.example.springboot_backend.notification.application.dto.*;
import com.example.springboot_backend.notification.application.service.*;
import com.example.springboot_backend.notification.domain.valueobject.NotificationChannel;
import com.example.springboot_backend.shared.response.ApiResponse;
import com.example.springboot_backend.shared.util.AuthHeader;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class NotificationController {
    private final SendNotificationApplicationService sendService;
    private final NotificationQueryApplicationService queryService;
    private final CreateInformatorApplicationService informatorService;
    public NotificationController(SendNotificationApplicationService sendService, NotificationQueryApplicationService queryService, CreateInformatorApplicationService informatorService) {
        this.sendService=sendService; this.queryService=queryService; this.informatorService=informatorService;
    }
    @PostMapping("/notifications/events/{eventId}")
    public ApiResponse<NotificationDto> send(@PathVariable UUID eventId, @RequestParam(required=false) NotificationChannel channel, @RequestHeader(value="Authorization", required=false) String auth) {
        return ApiResponse.ok(sendService.send(new SendNotificationCommand(AuthHeader.extractToken(auth), eventId, channel)));
    }
    @GetMapping("/notifications") public ApiResponse<List<NotificationDto>> mine(@RequestHeader(value="Authorization", required=false) String auth) { return ApiResponse.ok(queryService.mine(AuthHeader.extractToken(auth))); }
    @PostMapping("/informators/events/{eventId}/notifications/{notificationId}")
    public ApiResponse<InformatorDto> createInformator(@PathVariable UUID eventId, @PathVariable UUID notificationId, @RequestHeader(value="Authorization", required=false) String auth) { return ApiResponse.ok(informatorService.create(AuthHeader.extractToken(auth), eventId, notificationId)); }
    @GetMapping("/informators") public ApiResponse<List<InformatorDto>> informators(@RequestHeader(value="Authorization", required=false) String auth) { return ApiResponse.ok(informatorService.mine(AuthHeader.extractToken(auth))); }
}
