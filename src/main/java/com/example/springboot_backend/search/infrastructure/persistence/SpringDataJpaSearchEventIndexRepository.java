package com.example.springboot_backend.search.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.UUID;

public interface SpringDataJpaSearchEventIndexRepository extends JpaRepository<JpaSearchEventViewEntity, UUID> {
    @Query("select distinct e.category from JpaSearchEventViewEntity e where e.status = 'PUBLISHED'")
    List<String> findDistinctCategories();
    @Query("select distinct e.city from JpaSearchEventViewEntity e where e.status = 'PUBLISHED'")
    List<String> findDistinctCities();
}
