package com.example.springboot_backend.account.infrastructure.persistence;

import com.example.springboot_backend.account.domain.model.AccountStatus;
import com.example.springboot_backend.account.domain.model.UserAccount;
import com.example.springboot_backend.account.domain.valueobject.EmailAddress;
import com.example.springboot_backend.account.domain.valueobject.PasswordHash;
import com.example.springboot_backend.account.domain.valueobject.UserAccountId;
import com.example.springboot_backend.account.domain.valueobject.VerificationToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebMvcTest(DatabaseUserAccountRepository.class)
class DatabaseUserAccountRepositoryIT {

    @MockitoBean
    private SpringDataJpaUserAccountRepository springDataRepository;

    private DatabaseUserAccountRepository repository;

    @BeforeEach
    void setUp() {
        repository = new DatabaseUserAccountRepository(springDataRepository);
    }

    @Test
    void save() {
        // Given
        UserAccount account = createTestAccount();
        JpaUserAccountEntity entity = toEntity(account);
        when(springDataRepository.save(any(JpaUserAccountEntity.class))).thenReturn(entity);

        // When
        UserAccount savedAccount = repository.save(account);

        // Then
        assertThat(savedAccount).isNotNull();
        assertThat(savedAccount.id().value()).isEqualTo(account.id().value());
        assertThat(savedAccount.email().value()).isEqualTo(account.email().value());
    }

    @Test
    void findById() {
        // Given
        UserAccount account = createTestAccount();
        JpaUserAccountEntity entity = toEntity(account);
        when(springDataRepository.findById(account.id().value())).thenReturn(Optional.of(entity));

        // When
        Optional<UserAccount> result = repository.findById(account.id());

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().id().value()).isEqualTo(account.id().value());
    }

    @Test
    void findByEmail() {
        // Given
        UserAccount account = createTestAccount();
        JpaUserAccountEntity entity = toEntity(account);
        when(springDataRepository.findByEmail(account.email().value())).thenReturn(Optional.of(entity));

        // When
        Optional<UserAccount> result = repository.findByEmail(account.email());

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().email().value()).isEqualTo(account.email().value());
    }

    @Test
    void findByVerificationToken() {
        // Given
        UserAccount account = createTestAccount();
        JpaUserAccountEntity entity = toEntity(account);
        String token = account.verificationToken().value();
        when(springDataRepository.findByVerificationToken(token)).thenReturn(Optional.of(entity));

        // When
        Optional<UserAccount> result = repository.findByVerificationToken(token);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().verificationToken().value()).isEqualTo(token);
    }

    @Test
    void existsByEmail() {
        // Given
        EmailAddress email = EmailAddress.of("test@example.com");
        when(springDataRepository.existsByEmail(email.value())).thenReturn(true);

        // When
        boolean exists = repository.existsByEmail(email);

        // Then
        assertThat(exists).isTrue();
    }

    private UserAccount createTestAccount() {
        return new UserAccount(
                UserAccountId.newId(),
                EmailAddress.of("test@example.com"),
                PasswordHash.of("hashed-password"),
                AccountStatus.ACTIVE,
                Instant.now(),
                Instant.now(),
                new VerificationToken("token-123", Instant.now().plusSeconds(3600)),
                "123456789"
        );
    }

    private JpaUserAccountEntity toEntity(UserAccount account) {
        return new JpaUserAccountEntity(
                account.id().value(),
                account.email().value(),
                account.passwordHash().value(),
                account.status(),
                account.registeredAt(),
                account.activatedAt(),
                account.verificationToken().value(),
                account.verificationToken().expiresAt(),
                account.phoneNumber()
        );
    }
}