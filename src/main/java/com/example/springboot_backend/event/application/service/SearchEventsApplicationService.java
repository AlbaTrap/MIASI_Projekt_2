package com.example.springboot_backend.event.application.service;
import com.example.springboot_backend.event.application.dto.EventDto;
import com.example.springboot_backend.event.application.query.SearchEventsQuery;
import com.example.springboot_backend.event.domain.repository.EventRepository;
import com.example.springboot_backend.event.mapper.EventMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class SearchEventsApplicationService {
    private final EventRepository eventRepository;
    public SearchEventsApplicationService(EventRepository eventRepository) { this.eventRepository = eventRepository; }
    @Transactional(readOnly = true)
    public List<EventDto> search(SearchEventsQuery query) {
        return eventRepository.search(query.category(), query.city(), query.from(), query.to()).stream().map(EventMapper::toDto).toList();
    }
}
