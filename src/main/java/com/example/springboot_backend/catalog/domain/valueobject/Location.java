package com.example.springboot_backend.catalog.domain.valueobject;

public record Location(String placeName, Address address, Coordinates coordinates) {
    public Location {
        placeName = placeName == null ? "" : placeName.trim();
        if (address == null) throw new IllegalArgumentException("Adres jest wymagany");
    }
    public static Location of(String city, String streetAndNumber) {
        return new Location(streetAndNumber, new Address(streetAndNumber, "", city), null);
    }
    public static Location of(String city, String placeName, String streetAndNumber) {
        return new Location(placeName, new Address(streetAndNumber, "", city), null);
    }
    public boolean isInWroclaw() {
        String city = address.city();
        return city != null && (city.equalsIgnoreCase("Wrocław") || city.equalsIgnoreCase("Wroclaw"));
    }
    public String city() { return address.city(); }
    public String addressText() { return address.street(); }
}
