package com.example.springboot_backend.search.mapper;

import com.example.springboot_backend.search.application.dto.SearchResultDto;
import com.example.springboot_backend.search.domain.model.SearchEventView;

public final class SearchMapper {
    private SearchMapper() { }
    public static SearchResultDto toDto(SearchEventView view) {
        return new SearchResultDto(view.eventId(), view.title(), view.shortDescription(), view.startDate(), view.location(), view.category());
    }
}
