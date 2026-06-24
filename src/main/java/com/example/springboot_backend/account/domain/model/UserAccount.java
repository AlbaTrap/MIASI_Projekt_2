package com.example.springboot_backend.account.domain.model;

import com.example.springboot_backend.account.domain.valueobject.*;
import com.example.springboot_backend.shared.exception.BusinessException;
import java.time.Instant;

public class UserAccount {
    private final UserAccountId id;
    private final EmailAddress email;
    private PasswordHash passwordHash;
    private AccountStatus status;
    private final Instant registeredAt;
    private Instant activatedAt;
    private VerificationToken verificationToken;
    private String phoneNumber;

    public UserAccount(UserAccountId id, EmailAddress email, PasswordHash passwordHash,
                       AccountStatus status, Instant registeredAt, Instant activatedAt,
                       VerificationToken verificationToken, String phoneNumber) {
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.status = status;
        this.registeredAt = registeredAt;
        this.activatedAt = activatedAt;
        this.verificationToken = verificationToken;
        this.phoneNumber = phoneNumber;
    }

    public static UserAccount register(EmailAddress email, PasswordHash passwordHash, VerificationToken token) {
        return new UserAccount(UserAccountId.newId(), email, passwordHash,
                AccountStatus.PENDING_CONFIRMATION, Instant.now(), null, token, null);
    }

    public void confirmEmail(String tokenValue) {
        if (status != AccountStatus.PENDING_CONFIRMATION) throw new BusinessException("Konto nie oczekuje na potwierdzenie");
        if (verificationToken == null || verificationToken.expired() || !verificationToken.value().equals(tokenValue)) {
            throw new BusinessException("Token weryfikacyjny jest niepoprawny albo wygasł");
        }
        status = AccountStatus.ACTIVE;
        activatedAt = Instant.now();
        verificationToken = null;
    }

    public void changePassword(PasswordHash newPasswordHash) {
        if (status == AccountStatus.DELETED) throw new BusinessException("Nie można zmienić hasła usuniętego konta");
        passwordHash = newPasswordHash;
    }

    public void block(BlockReason reason) {
        if (status == AccountStatus.DELETED) throw new BusinessException("Nie można zablokować usuniętego konta");
        status = AccountStatus.BLOCKED;
    }

    public void delete() { status = AccountStatus.DELETED; }

    public void changePhoneNumber(String newPhoneNumber) {
        if (status == AccountStatus.DELETED) throw new BusinessException("Nie można zmienić numeru telefonu usuniętego konta");
        phoneNumber = newPhoneNumber == null || newPhoneNumber.isBlank() ? null : newPhoneNumber.trim();
    }

    public boolean active() { return status == AccountStatus.ACTIVE; }

    public UserAccountId id() { return id; }
    public EmailAddress email() { return email; }
    public PasswordHash passwordHash() { return passwordHash; }
    public AccountStatus status() { return status; }
    public Instant registeredAt() { return registeredAt; }
    public Instant activatedAt() { return activatedAt; }
    public VerificationToken verificationToken() { return verificationToken; }
    public String phoneNumber() { return phoneNumber; }
}
