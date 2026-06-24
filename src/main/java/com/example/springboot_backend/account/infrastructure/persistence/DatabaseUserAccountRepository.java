package com.example.springboot_backend.account.infrastructure.persistence;

import com.example.springboot_backend.account.domain.model.UserAccount;
import com.example.springboot_backend.account.domain.repository.UserAccountRepository;
import com.example.springboot_backend.account.domain.valueobject.*;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public class DatabaseUserAccountRepository implements UserAccountRepository {
    private final SpringDataJpaUserAccountRepository repository;

    public DatabaseUserAccountRepository(SpringDataJpaUserAccountRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserAccount save(UserAccount account) {
        return toDomain(repository.save(toJpa(account)));
    }

    @Override
    public Optional<UserAccount> findById(UserAccountId id) {
        return repository.findById(id.value()).map(this::toDomain);
    }

    @Override
    public Optional<UserAccount> findByEmail(EmailAddress email) {
        return repository.findByEmail(email.value()).map(this::toDomain);
    }

    @Override
    public Optional<UserAccount> findByVerificationToken(String token) {
        return repository.findByVerificationToken(token).map(this::toDomain);
    }

    @Override
    public boolean existsByEmail(EmailAddress email) { return repository.existsByEmail(email.value()); }

    private JpaUserAccountEntity toJpa(UserAccount account) {
        VerificationToken vt = account.verificationToken();
        return new JpaUserAccountEntity(
                account.id().value(), account.email().value(), account.passwordHash().value(), account.status(),
                account.registeredAt(), account.activatedAt(), vt == null ? null : vt.value(), vt == null ? null : vt.expiresAt(), account.phoneNumber());
    }

    private UserAccount toDomain(JpaUserAccountEntity entity) {
        VerificationToken vt = entity.getVerificationToken() == null ? null :
                new VerificationToken(entity.getVerificationToken(), entity.getVerificationTokenExpiresAt());
        return new UserAccount(UserAccountId.of(entity.getId()), EmailAddress.of(entity.getEmail()),
                PasswordHash.of(entity.getPasswordHash()), entity.getStatus(), entity.getRegisteredAt(),
                entity.getActivatedAt(), vt, entity.getPhoneNumber());
    }
}
