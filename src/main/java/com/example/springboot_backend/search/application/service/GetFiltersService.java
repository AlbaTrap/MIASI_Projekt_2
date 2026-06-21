package com.example.springboot_backend.search.application.service;

import com.example.springboot_backend.search.application.dto.FilterOptionsDto;
import com.example.springboot_backend.search.domain.repository.EventIndexRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetFiltersService {
    private final EventIndexRepository repository;
    public GetFiltersService(EventIndexRepository repository) { this.repository = repository; }
    @Transactional(readOnly = true)
    public FilterOptionsDto getFilters() { return new FilterOptionsDto(repository.getCategories(), repository.getLocations()); }
}
