package com.example.springboot_backend.event.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;
interface SpringDataJpaRawEventRepository extends JpaRepository<JpaRawEventEntity, UUID> { List<JpaRawEventEntity> findByProcessedFalse(); }
