package com.example.springboot_backend.search.application.dto;

import java.util.List;

public record FilterOptionsDto(List<String> categories, List<String> locations) { }
