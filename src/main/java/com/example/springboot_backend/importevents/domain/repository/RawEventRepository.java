package com.example.springboot_backend.importevents.domain.repository;

import com.example.springboot_backend.importevents.domain.model.RawEvent;
import com.example.springboot_backend.importevents.domain.valueobject.RawEventId;
import java.util.List;
import java.util.Optional;

public interface RawEventRepository {
    RawEvent save(RawEvent rawEvent);
    List<RawEvent> saveAll(List<RawEvent> rawEvents);
    Optional<RawEvent> findById(RawEventId id);
    List<RawEvent> findUnprocessed();
}
