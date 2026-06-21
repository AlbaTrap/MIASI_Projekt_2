package com.example.springboot_backend.importevents.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface SpringDataJpaRawEventRepository extends JpaRepository<JpaRawEventEntity, UUID> {
    List<JpaRawEventEntity> findByProcessedFalse();
}
