package com.example.springboot_backend.search.domain.service;

import com.example.springboot_backend.search.domain.model.SearchEventView;
import com.example.springboot_backend.search.domain.valueobject.*;
import org.springframework.stereotype.Component;
import java.util.Comparator;
import java.util.List;

@Component
public class ResultsSortingService {
    public List<SearchEventView> sort(List<SearchEventView> results, Sorting sorting) {
        Comparator<SearchEventView> comparator = switch (sorting.field()) {
            case NAME -> Comparator.comparing(SearchEventView::title, String.CASE_INSENSITIVE_ORDER);
            case CATEGORY -> Comparator.comparing(SearchEventView::category, String.CASE_INSENSITIVE_ORDER);
            case DATE -> Comparator.comparing(SearchEventView::startDate);
        };
        if (sorting.direction() == SortDirection.DESC) comparator = comparator.reversed();
        return results.stream().sorted(comparator).toList();
    }
}
