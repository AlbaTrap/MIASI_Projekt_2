package com.example.springboot_backend.event.domain.repository;
import com.example.springboot_backend.event.domain.model.RawEvent;
import java.util.List;
public interface RawEventRepository {
    RawEvent save(RawEvent rawEvent);
    List<RawEvent> saveAll(List<RawEvent> rawEvents);
    List<RawEvent> findUnprocessed();
}
