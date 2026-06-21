package com.example.springboot_backend.search.domain.repository;

import com.example.springboot_backend.search.domain.model.SearchEventView;
import com.example.springboot_backend.search.domain.valueobject.SearchQuery;
import java.util.List;
import java.util.UUID;

public interface EventIndexRepository {
    List<SearchEventView> search(SearchQuery query);
    SearchEventView saveView(SearchEventView view);
    void remove(UUID eventId);
    List<String> getCategories();
    List<String> getLocations();
    void clear();
}
