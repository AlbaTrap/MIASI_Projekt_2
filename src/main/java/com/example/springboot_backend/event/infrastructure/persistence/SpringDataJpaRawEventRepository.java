package com.example.springboot_backend.event.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository("eventSpringDataJpaRawEventRepository")
interface SpringDataJpaRawEventRepository extends JpaRepository<JpaRawEventEntity, UUID> { List<JpaRawEventEntity> findByProcessedFalse(); }
