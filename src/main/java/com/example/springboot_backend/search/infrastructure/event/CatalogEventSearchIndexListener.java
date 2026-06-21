package com.example.springboot_backend.search.infrastructure.event;

import com.example.springboot_backend.catalog.domain.event.*;
import com.example.springboot_backend.search.application.service.UpdateSearchIndexService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class CatalogEventSearchIndexListener {
    private final UpdateSearchIndexService indexService;

    public CatalogEventSearchIndexListener(UpdateSearchIndexService indexService) {
        this.indexService = indexService;
    }

    @EventListener
    public void on(EventPublishedEvent event) {
        indexService.addToIndex(event.eventId());
    }

    @EventListener
    public void on(EventUpdatedEvent event) {
        indexService.updateInIndex(event.eventId());
    }

    @EventListener
    public void on(EventHiddenEvent event) {
        indexService.removeFromIndex(event.eventId());
    }

    @EventListener
    public void on(EventCancelledEvent event) {
        indexService.removeFromIndex(event.eventId());
    }

    @EventListener
    public void on(EventArchivedEvent event) {
        indexService.removeFromIndex(event.eventId());
    }
}
