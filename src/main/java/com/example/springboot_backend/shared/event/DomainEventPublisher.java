package com.example.springboot_backend.shared.event;

public interface DomainEventPublisher {
    void publish(DomainEvent event);
}
