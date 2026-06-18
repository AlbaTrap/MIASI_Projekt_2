package com.example.springboot_backend.account.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

interface SpringDataJpaUserAccountRepository extends JpaRepository<JpaUserAccountEntity, UUID> {
    Optional<JpaUserAccountEntity> findByEmail(String email);
    Optional<JpaUserAccountEntity> findByVerificationToken(String token);
    boolean existsByEmail(String email);
}
