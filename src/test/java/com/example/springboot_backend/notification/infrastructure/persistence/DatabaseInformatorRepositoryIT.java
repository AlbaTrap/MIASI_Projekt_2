package com.example.springboot_backend.notification.infrastructure.persistence;

import com.example.springboot_backend.notification.domain.model.Informator;
import com.example.springboot_backend.notification.domain.valueobject.InformatorId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest(DatabaseInformatorRepository.class)
class DatabaseInformatorRepositoryIT {

    @Autowired
    private DatabaseInformatorRepository databaseInformatorRepository;

    @MockitoBean
    private SpringDataJpaInformatorRepository springDataJpaInformatorRepository;

    @Test
    void save() {
        // given
        Informator informator = createInformator();
        JpaInformatorEntity entity = toEntity(informator);
        when(springDataJpaInformatorRepository.save(any())).thenReturn(entity);

        // when
        Informator saved = databaseInformatorRepository.save(informator);

        // then
        assertNotNull(saved);
        assertEquals(informator.id().value(), saved.id().value());
        assertEquals(informator.userId(), saved.userId());
        assertEquals(informator.eventId(), saved.eventId());
        assertEquals(informator.notificationId(), saved.notificationId());
        assertEquals(informator.message(), saved.message());
        verify(springDataJpaInformatorRepository).save(any());
    }

    @Test
    void findByUserId() {
        // given
        UUID userId = UUID.randomUUID();
        Informator informator = createInformatorWithUser(userId);
        JpaInformatorEntity entity = toEntity(informator);
        when(springDataJpaInformatorRepository.findByUserId(userId)).thenReturn(List.of(entity));

        // when
        List<Informator> results = databaseInformatorRepository.findByUserId(userId);

        // then
        assertEquals(1, results.size());
        assertEquals(informator.id().value(), results.get(0).id().value());
        assertEquals(userId, results.get(0).userId());
        verify(springDataJpaInformatorRepository).findByUserId(userId);
    }

    private Informator createInformator() {
        return createInformatorWithUser(UUID.randomUUID());
    }

    private Informator createInformatorWithUser(UUID userId) {
        return new Informator(
                InformatorId.of(UUID.randomUUID()),
                userId,
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Informator Message",
                Instant.now()
        );
    }

    private JpaInformatorEntity toEntity(Informator i) {
        return new JpaInformatorEntity(
                i.id().value(),
                i.userId(),
                i.eventId(),
                i.notificationId(),
                i.message(),
                i.createdAt()
        );
    }
}