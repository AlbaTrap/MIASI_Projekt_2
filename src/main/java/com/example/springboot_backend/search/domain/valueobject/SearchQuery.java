package com.example.springboot_backend.search.domain.valueobject;

public record SearchQuery(SearchPhrase phrase, SearchCriteria criteria, Sorting sorting, Pagination pagination) {
    public SearchQuery {
        if (phrase == null) phrase = new SearchPhrase("");
        if (criteria == null) criteria = new SearchCriteria(null, new DateRange(null, null), null, "PUBLISHED");
        if (sorting == null) sorting = new Sorting(null, null);
        if (pagination == null) pagination = new Pagination(0, 20);
    }
    public boolean isEmpty() { return phrase.isBlank() && !hasFilters(); }
    public boolean hasFilters() {
        return criteria.category() != null || criteria.city() != null || criteria.dateRange().from() != null || criteria.dateRange().to() != null;
    }
}
