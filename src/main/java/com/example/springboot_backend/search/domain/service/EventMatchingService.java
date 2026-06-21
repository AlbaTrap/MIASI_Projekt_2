package com.example.springboot_backend.search.domain.service;

import com.example.springboot_backend.search.domain.model.SearchEventView;
import com.example.springboot_backend.search.domain.valueobject.SearchQuery;
import org.springframework.stereotype.Component;

@Component
public class EventMatchingService {
    public boolean matches(SearchEventView view, SearchQuery query) {
        boolean phraseOk = query.phrase().isBlank()
                || contains(view.title(), query.phrase().value())
                || contains(view.shortDescription(), query.phrase().value());
        boolean categoryOk = query.criteria().category() == null || query.criteria().category().isBlank() || view.category().equalsIgnoreCase(query.criteria().category());
        boolean cityOk = query.criteria().city() == null || query.criteria().city().isBlank() || view.city().equalsIgnoreCase(query.criteria().city());
        boolean dateOk = query.criteria().dateRange() == null || query.criteria().dateRange().contains(view.startDate());
        boolean statusOk = view.status().equalsIgnoreCase("PUBLISHED");
        return phraseOk && categoryOk && cityOk && dateOk && statusOk;
    }
    private boolean contains(String source, String phrase) { return source != null && phrase != null && source.toLowerCase().contains(phrase.toLowerCase()); }
}
