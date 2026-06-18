package com.example.springboot_backend.account.domain.repository;

import com.example.springboot_backend.account.domain.model.UserAccount;
import com.example.springboot_backend.account.domain.valueobject.EmailAddress;
import com.example.springboot_backend.account.domain.valueobject.UserAccountId;
import java.util.Optional;

public interface UserAccountRepository {
    UserAccount save(UserAccount account);
    Optional<UserAccount> findById(UserAccountId id);
    Optional<UserAccount> findByEmail(EmailAddress email);
    Optional<UserAccount> findByVerificationToken(String token);
    boolean existsByEmail(EmailAddress email);
}
