package com.example.springboot_backend.notification.application.service;
import com.example.springboot_backend.account.application.port.AccountAccessPort;
import com.example.springboot_backend.notification.application.dto.NotificationDto;
import com.example.springboot_backend.notification.domain.repository.NotificationRepository;
import com.example.springboot_backend.notification.mapper.NotificationMapper;
import com.example.springboot_backend.shared.exception.UnauthorizedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;
@Service
public class NotificationQueryApplicationService {
    private final AccountAccessPort accountAccessPort;
    private final NotificationRepository repository;
    public NotificationQueryApplicationService(AccountAccessPort accountAccessPort, NotificationRepository repository) { this.accountAccessPort=accountAccessPort; this.repository=repository; }
    @Transactional(readOnly = true)
    public List<NotificationDto> mine(String accessToken) {
        UUID userId = accountAccessPort.findAccountIdByAccessToken(accessToken).orElseThrow(() -> new UnauthorizedException("Zaloguj się"));
        return repository.findByUserId(userId).stream().map(NotificationMapper::toDto).toList();
    }
}
