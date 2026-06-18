package com.example.springboot_backend.event.infrastructure.persistence;
import com.example.springboot_backend.event.domain.model.EventStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;
interface SpringDataJpaEventRepository extends JpaRepository<JpaEventEntity, UUID> {
    List<JpaEventEntity> findByStatus(EventStatus status);
}
