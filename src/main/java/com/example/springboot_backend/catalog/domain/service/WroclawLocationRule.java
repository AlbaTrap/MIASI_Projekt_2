package com.example.springboot_backend.catalog.domain.service;

import com.example.springboot_backend.catalog.domain.valueobject.Location;
import org.springframework.stereotype.Component;

@Component
public class WroclawLocationRule {
    public boolean isSatisfied(Location location) {
        return location != null && location.isInWroclaw();
    }
}
