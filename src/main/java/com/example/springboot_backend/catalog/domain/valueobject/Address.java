package com.example.springboot_backend.catalog.domain.valueobject;

public record Address(String street, String number, String city) {
    public Address {
        street = street == null ? "" : street.trim();
        number = number == null ? "" : number.trim();
        if (city == null || city.isBlank()) throw new IllegalArgumentException("Miasto jest wymagane");
        city = city.trim();
    }
}
