package com.example.springboot_backend.search.domain.valueobject;

public record SearchCriteria(String category, DateRange dateRange, String city, String status) { }
