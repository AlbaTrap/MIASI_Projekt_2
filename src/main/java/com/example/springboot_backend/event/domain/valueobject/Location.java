package com.example.springboot_backend.event.domain.valueobject;
import com.example.springboot_backend.shared.exception.BusinessException;
public record Location(String city, String address) {
    public Location {
        if (city == null || city.isBlank()) throw new BusinessException("Miasto wydarzenia jest wymagane");
        city = city.trim();
        address = address == null ? "" : address.trim();
    }
    public boolean inWroclaw() { return city.equalsIgnoreCase("Wrocław") || city.equalsIgnoreCase("Wroclaw"); }
}
