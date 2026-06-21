package com.example.springboot_backend.catalog.domain.repository;

import com.example.springboot_backend.catalog.domain.model.CatalogEvent;
import com.example.springboot_backend.catalog.domain.valueobject.CatalogEventId;
import java.util.List;
import java.util.Optional;

public interface CatalogEventRepository {
    CatalogEvent save(CatalogEvent event);
    List<CatalogEvent> saveAll(List<CatalogEvent> events);
    Optional<CatalogEvent> findById(CatalogEventId id);
    List<CatalogEvent> findPublished();
    List<CatalogEvent> findForIndexing();
    boolean existsById(CatalogEventId id);
    void delete(CatalogEventId id);
}
