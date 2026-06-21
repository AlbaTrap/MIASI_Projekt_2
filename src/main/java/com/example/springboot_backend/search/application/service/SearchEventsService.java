package com.example.springboot_backend.search.application.service;

import com.example.springboot_backend.search.application.dto.SearchResultsListDto;
import com.example.springboot_backend.search.application.query.SearchEventsQuery;
import com.example.springboot_backend.search.domain.repository.EventIndexRepository;
import com.example.springboot_backend.search.domain.valueobject.*;
import com.example.springboot_backend.search.mapper.SearchMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SearchEventsService {
    private final EventIndexRepository repository;
    public SearchEventsService(EventIndexRepository repository) { this.repository = repository; }
    @Transactional(readOnly = true)
    public SearchResultsListDto search(SearchEventsQuery q) {
        SearchQuery query = toDomain(q);
        var all = repository.search(query);
        var page = all.stream().skip(query.pagination().offset()).limit(query.pagination().pageSize()).map(SearchMapper::toDto).toList();
        return new SearchResultsListDto(page, all.size(), query.pagination().pageNumber(), query.pagination().pageSize());
    }
    private SearchQuery toDomain(SearchEventsQuery q) {
        SortField field = parseSortField(q.sortBy());
        SortDirection direction = "DESC".equalsIgnoreCase(q.direction()) || "MALEJACO".equalsIgnoreCase(q.direction()) ? SortDirection.DESC : SortDirection.ASC;
        return new SearchQuery(new SearchPhrase(q.phrase()), new SearchCriteria(q.category(), new DateRange(q.from(), q.to()), q.city(), "PUBLISHED"), new Sorting(field, direction), new Pagination(q.page(), q.size()));
    }
    private SortField parseSortField(String sortBy) {
        if (sortBy == null) return SortField.DATE;
        return switch (sortBy.trim().toUpperCase()) {
            case "NAME", "NAZWA" -> SortField.NAME;
            case "CATEGORY", "KATEGORIA" -> SortField.CATEGORY;
            default -> SortField.DATE;
        };
    }
}
