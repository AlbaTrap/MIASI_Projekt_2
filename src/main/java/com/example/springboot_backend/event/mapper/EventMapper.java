package com.example.springboot_backend.event.mapper;
import com.example.springboot_backend.event.application.dto.EventDto;
import com.example.springboot_backend.event.application.dto.EventSnapshot;
import com.example.springboot_backend.event.domain.model.Event;
public final class EventMapper {
    private EventMapper() {}
    public static EventDto toDto(Event e) {
        return new EventDto(e.id().value(), e.title().value(), e.description(), e.location().city(), e.location().address(),
                e.date().startDate(), e.date().endDate(), e.category(), e.status());
    }
    public static EventSnapshot toSnapshot(Event e) {
        return new EventSnapshot(e.id().value(), e.title().value(), e.date().startDate(), e.location().city(), e.location().address(), e.category());
    }
}
