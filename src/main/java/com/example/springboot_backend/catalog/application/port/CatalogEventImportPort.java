package com.example.springboot_backend.catalog.application.port;

import com.example.springboot_backend.catalog.application.command.AddImportedEventCommand;
import com.example.springboot_backend.catalog.application.dto.CatalogEventDto;

public interface CatalogEventImportPort {
    CatalogEventDto addImportedEvent(AddImportedEventCommand command);
}
