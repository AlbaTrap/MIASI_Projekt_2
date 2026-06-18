package com.example.springboot_backend.notification.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;
interface SpringDataJpaInformatorRepository extends JpaRepository<JpaInformatorEntity, UUID> { List<JpaInformatorEntity> findByUserId(UUID userId); }
