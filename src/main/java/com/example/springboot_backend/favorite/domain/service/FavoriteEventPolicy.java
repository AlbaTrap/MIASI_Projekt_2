package com.example.springboot_backend.favorite.domain.service;
import com.example.springboot_backend.shared.exception.BusinessException;
import org.springframework.stereotype.Component;
import java.util.UUID;
@Component
public class FavoriteEventPolicy {
    public void checkCanAdd(UUID userId, UUID eventId, boolean userLoggedIn, boolean eventAvailable, boolean alreadyExists) {
        if (userId == null) throw new BusinessException("Użytkownik jest wymagany");
        if (eventId == null) throw new BusinessException("Wydarzenie jest wymagane");
        if (!userLoggedIn) throw new BusinessException("Użytkownik musi być zalogowany");
        if (!eventAvailable) throw new BusinessException("Wydarzenie nie istnieje albo nie jest dostępne");
        if (alreadyExists) throw new BusinessException("Wydarzenie jest już w ulubionych");
    }
}
