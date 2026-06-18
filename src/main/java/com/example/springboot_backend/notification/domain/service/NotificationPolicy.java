package com.example.springboot_backend.notification.domain.service;
import com.example.springboot_backend.shared.exception.BusinessException;
import org.springframework.stereotype.Component;
@Component
public class NotificationPolicy {
    public void checkCanSend(boolean favorite, boolean hasContactData) {
        if (!favorite) throw new BusinessException("Powiadomienie można wysłać tylko dla wydarzenia dodanego do ulubionych");
        if (!hasContactData) throw new BusinessException("Brak danych kontaktowych użytkownika");
    }
}
