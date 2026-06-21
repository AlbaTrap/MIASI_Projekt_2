package com.example.springboot_backend.shared.api;

import com.example.springboot_backend.shared.response.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {
    @GetMapping("/")
    public ApiResponse<String> home() {
        return ApiResponse.ok(
                "Backend działa poprawnie",
                "DDD contexts: /api/catalog/events, /api/events, /api/admin/import/events/fetch, /api/auth/register, /h2-console"
        );
    }
}
