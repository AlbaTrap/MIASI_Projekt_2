package com.example.springboot_backend.search.application.dto;

import java.util.List;

public record SearchResultsListDto(List<SearchResultDto> results, long totalResults, int pageNumber, int pageSize) { }
