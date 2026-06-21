package com.example.springboot_backend.search.infrastructure.persistence;

import com.example.springboot_backend.search.domain.model.SearchEventView;
import com.example.springboot_backend.search.domain.repository.EventIndexRepository;
import com.example.springboot_backend.search.domain.service.EventMatchingService;
import com.example.springboot_backend.search.domain.service.ResultsSortingService;
import com.example.springboot_backend.search.domain.valueobject.SearchQuery;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public class DatabaseEventIndexRepository implements EventIndexRepository {
    private final SpringDataJpaSearchEventIndexRepository repository;
    private final EventMatchingService matchingService;
    private final ResultsSortingService sortingService;
    public DatabaseEventIndexRepository(SpringDataJpaSearchEventIndexRepository repository, EventMatchingService matchingService, ResultsSortingService sortingService) {
        this.repository = repository; this.matchingService = matchingService; this.sortingService = sortingService;
    }
    @Override public List<SearchEventView> search(SearchQuery query) {
        var filtered = repository.findAll().stream().map(this::toDomain).filter(v -> matchingService.matches(v, query)).toList();
        return sortingService.sort(filtered, query.sorting());
    }
    @Override public SearchEventView saveView(SearchEventView view) { return toDomain(repository.save(toJpa(view))); }
    @Override public void remove(UUID eventId) { repository.deleteById(eventId); }
    @Override public List<String> getCategories() { return repository.findDistinctCategories().stream().filter(v -> v != null && !v.isBlank()).sorted(String.CASE_INSENSITIVE_ORDER).toList(); }
    @Override public List<String> getLocations() { return repository.findDistinctCities().stream().filter(v -> v != null && !v.isBlank()).sorted(String.CASE_INSENSITIVE_ORDER).toList(); }
    @Override public void clear() { repository.deleteAll(); }
    private JpaSearchEventViewEntity toJpa(SearchEventView v) { return new JpaSearchEventViewEntity(v.eventId(), v.title(), v.shortDescription(), v.startDate(), v.city(), v.location(), v.category(), v.status()); }
    private SearchEventView toDomain(JpaSearchEventViewEntity e) { return new SearchEventView(e.getEventId(), e.getTitle(), e.getShortDescription(), e.getStartDate(), e.getCity(), e.getLocation(), e.getCategory(), e.getStatus()); }
}
