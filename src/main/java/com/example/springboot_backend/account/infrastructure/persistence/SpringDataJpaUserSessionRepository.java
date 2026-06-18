package com.example.springboot_backend.account.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface SpringDataJpaUserSessionRepository extends JpaRepository<JpaUserSessionEntity, UUID> {
    Optional<JpaUserSessionEntity> findByToken(String token);
    List<JpaUserSessionEntity> findByAccountId(UUID accountId);
}
