package com.example.springboot_backend.catalog.infrastructure.persistence;

import com.example.springboot_backend.catalog.domain.model.EventStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface SpringDataJpaCatalogEventRepository extends JpaRepository<JpaCatalogEventEntity, UUID> {
    List<JpaCatalogEventEntity> findByStatus(EventStatus status);
}
