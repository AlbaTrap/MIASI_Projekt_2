package com.example.springboot_backend.notification.domain.repository;
import com.example.springboot_backend.notification.domain.model.Informator;
import java.util.List;
import java.util.UUID;
public interface InformatorRepository {
    Informator save(Informator informator);
    List<Informator> findByUserId(UUID userId);
}
