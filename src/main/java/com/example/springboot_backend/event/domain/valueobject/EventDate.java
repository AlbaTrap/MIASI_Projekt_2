package com.example.springboot_backend.event.domain.valueobject;
import com.example.springboot_backend.shared.exception.BusinessException;
import java.time.Instant;
public record EventDate(Instant startDate, Instant endDate) {
    public EventDate {
        if (startDate == null) throw new BusinessException("Data rozpoczęcia wydarzenia jest wymagana");
        if (endDate != null && endDate.isBefore(startDate)) throw new BusinessException("Data zakończenia nie może być przed datą rozpoczęcia");
    }
}
