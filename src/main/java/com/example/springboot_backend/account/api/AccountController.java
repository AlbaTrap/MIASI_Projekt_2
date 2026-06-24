package com.example.springboot_backend.account.api;

import com.example.springboot_backend.account.api.dto.BlockAccountRequest;
import com.example.springboot_backend.account.api.dto.ChangePasswordRequest;
import com.example.springboot_backend.account.api.dto.ChangePhoneNumberRequest;
import com.example.springboot_backend.account.application.command.BlockAccountCommand;
import com.example.springboot_backend.account.application.command.ChangePasswordCommand;
import com.example.springboot_backend.account.application.command.ChangePhoneNumberCommand;
import com.example.springboot_backend.account.application.command.DeleteAccountCommand;
import com.example.springboot_backend.account.application.dto.UserDto;
import com.example.springboot_backend.account.application.service.AccountManagementApplicationService;
import com.example.springboot_backend.account.application.service.AccountQueryApplicationService;
import com.example.springboot_backend.shared.response.ApiResponse;
import com.example.springboot_backend.shared.security.AdminAuthorizationService;
import com.example.springboot_backend.shared.util.AuthHeader;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/account")
public class AccountController {
    private final AccountQueryApplicationService queryService;
    private final AccountManagementApplicationService managementService;
    private final AdminAuthorizationService adminAuthorizationService;

    public AccountController(AccountQueryApplicationService queryService, AccountManagementApplicationService managementService, AdminAuthorizationService adminAuthorizationService) {
        this.queryService = queryService;
        this.managementService = managementService;
        this.adminAuthorizationService = adminAuthorizationService;
    }

    @GetMapping("/me")
    public ApiResponse<UserDto> me(@RequestHeader(value = "Authorization", required = false) String authorization) {
        return ApiResponse.ok(queryService.me(AuthHeader.extractToken(authorization)));
    }

    @PutMapping("/password")
    public ApiResponse<Void> changePassword(@RequestHeader(value = "Authorization", required = false) String authorization,
                                            @Valid @RequestBody ChangePasswordRequest request) {
        managementService.changePassword(new ChangePasswordCommand(AuthHeader.extractToken(authorization), request.oldPassword(), request.newPassword()));
        return ApiResponse.ok("Hasło zostało zmienione, aktywne sesje unieważniono");
    }

    @DeleteMapping("/me")
    public ApiResponse<Void> deleteMe(@RequestHeader(value = "Authorization", required = false) String authorization) {
        managementService.delete(new DeleteAccountCommand(AuthHeader.extractToken(authorization)));
        return ApiResponse.ok("Konto zostało oznaczone jako usunięte");
    }

    @PutMapping("/phone-number")
    public ApiResponse<Void> changePhoneNumber(@RequestHeader(value = "Authorization", required = false) String authorization,
                                               @RequestBody ChangePhoneNumberRequest request) {
        managementService.changePhoneNumber(new ChangePhoneNumberCommand(AuthHeader.extractToken(authorization), request.phoneNumber()));
        return ApiResponse.ok("Numer telefonu został zapisany");
    }

    @PatchMapping("/admin/{accountId}/block")
    public ApiResponse<Void> block(@PathVariable UUID accountId, @RequestHeader(value = "Authorization", required = false) String authorization,
                                   @RequestBody BlockAccountRequest request) {
        adminAuthorizationService.check(AuthHeader.extractToken(authorization));
        managementService.block(new BlockAccountCommand(accountId, request.reason()));
        return ApiResponse.ok("Konto zostało zablokowane");
    }
}
