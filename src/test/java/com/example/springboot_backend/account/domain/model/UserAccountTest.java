package com.example.springboot_backend.account.domain.model;

import com.example.springboot_backend.account.domain.valueobject.*;
import com.example.springboot_backend.shared.exception.BusinessException;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

class UserAccountTest {

    @Test
    void register_ShouldInitializeCorrectly() {
        // given
        EmailAddress email = EmailAddress.of("test@example.com");
        PasswordHash passwordHash = PasswordHash.of("hashedPassword");
        VerificationToken token = new VerificationToken("token123", Instant.now().plus(1, ChronoUnit.HOURS));

        // when
        UserAccount account = UserAccount.register(email, passwordHash, token);

        // then
        assertNotNull(account.id());
        assertEquals(email, account.email());
        assertEquals(passwordHash, account.passwordHash());
        assertEquals(AccountStatus.PENDING_CONFIRMATION, account.status());
        assertNotNull(account.registeredAt());
        assertNull(account.activatedAt());
        assertEquals(token, account.verificationToken());
        assertNull(account.phoneNumber());
    }

    @Test
    void confirmEmail_ShouldActivateAccount_WhenTokenIsValid() {
        // given
        String tokenValue = "token123";
        VerificationToken token = new VerificationToken(tokenValue, Instant.now().plus(1, ChronoUnit.HOURS));
        UserAccount account = UserAccount.register(EmailAddress.of("test@example.com"), PasswordHash.of("hash"), token);

        // when
        account.confirmEmail(tokenValue);

        // then
        assertEquals(AccountStatus.ACTIVE, account.status());
        assertNotNull(account.activatedAt());
        assertNull(account.verificationToken());
        assertTrue(account.active());
    }

    @Test
    void confirmEmail_ShouldThrowException_WhenAccountIsNotPending() {
        // given
        UserAccount account = UserAccount.register(EmailAddress.of("test@example.com"), PasswordHash.of("hash"), null);
        account.delete();

        // when & then
        BusinessException exception = assertThrows(BusinessException.class, () -> account.confirmEmail("token"));
        assertEquals("Konto nie oczekuje na potwierdzenie", exception.getMessage());
    }

    @Test
    void confirmEmail_ShouldThrowException_WhenTokenIsInvalid() {
        // given
        VerificationToken token = new VerificationToken("validToken", Instant.now().plus(1, ChronoUnit.HOURS));
        UserAccount account = UserAccount.register(EmailAddress.of("test@example.com"), PasswordHash.of("hash"), token);

        // when & then
        BusinessException exception = assertThrows(BusinessException.class, () -> account.confirmEmail("invalidToken"));
        assertEquals("Token weryfikacyjny jest niepoprawny albo wygasł", exception.getMessage());
    }

    @Test
    void confirmEmail_ShouldThrowException_WhenTokenIsExpired() {
        // given
        VerificationToken token = new VerificationToken("token", Instant.now().minus(1, ChronoUnit.HOURS));
        UserAccount account = UserAccount.register(EmailAddress.of("test@example.com"), PasswordHash.of("hash"), token);

        // when & then
        BusinessException exception = assertThrows(BusinessException.class, () -> account.confirmEmail("token"));
        assertEquals("Token weryfikacyjny jest niepoprawny albo wygasł", exception.getMessage());
    }

    @Test
    void changePassword_ShouldUpdatePassword() {
        // given
        UserAccount account = UserAccount.register(EmailAddress.of("test@example.com"), PasswordHash.of("oldHash"), null);
        PasswordHash newHash = PasswordHash.of("newHash");

        // when
        account.changePassword(newHash);

        // then
        assertEquals(newHash, account.passwordHash());
    }

    @Test
    void changePassword_ShouldThrowException_WhenAccountIsDeleted() {
        // given
        UserAccount account = UserAccount.register(EmailAddress.of("test@example.com"), PasswordHash.of("hash"), null);
        account.delete();

        // when & then
        BusinessException exception = assertThrows(BusinessException.class, () -> account.changePassword(PasswordHash.of("new")));
        assertEquals("Nie można zmienić hasła usuniętego konta", exception.getMessage());
    }

    @Test
    void block_ShouldChangeStatusToBlocked() {
        // given
        UserAccount account = UserAccount.register(EmailAddress.of("test@example.com"), PasswordHash.of("hash"), null);

        // when
        account.block(new BlockReason("Spam"));

        // then
        assertEquals(AccountStatus.BLOCKED, account.status());
        assertFalse(account.active());
    }

    @Test
    void block_ShouldThrowException_WhenAccountIsDeleted() {
        // given
        UserAccount account = UserAccount.register(EmailAddress.of("test@example.com"), PasswordHash.of("hash"), null);
        account.delete();

        // when & then
        BusinessException exception = assertThrows(BusinessException.class, () -> account.block(new BlockReason("Reason")));
        assertEquals("Nie można zablokować usuniętego konta", exception.getMessage());
    }

    @Test
    void delete_ShouldChangeStatusToDeleted() {
        // given
        UserAccount account = UserAccount.register(EmailAddress.of("test@example.com"), PasswordHash.of("hash"), null);

        // when
        account.delete();

        // then
        assertEquals(AccountStatus.DELETED, account.status());
        assertFalse(account.active());
    }

    @Test
    void changePhoneNumber_ShouldUpdatePhoneNumber() {
        // given
        UserAccount account = UserAccount.register(EmailAddress.of("test@example.com"), PasswordHash.of("hash"), null);

        // when
        account.changePhoneNumber(" 123456789 ");

        // then
        assertEquals("123456789", account.phoneNumber());
    }

    @Test
    void changePhoneNumber_ShouldSetNull_WhenInputIsBlank() {
        // given
        UserAccount account = UserAccount.register(EmailAddress.of("test@example.com"), PasswordHash.of("hash"), null);

        // when
        account.changePhoneNumber("   ");

        // then
        assertNull(account.phoneNumber());
    }

    @Test
    void changePhoneNumber_ShouldThrowException_WhenAccountIsDeleted() {
        // given
        UserAccount account = UserAccount.register(EmailAddress.of("test@example.com"), PasswordHash.of("hash"), null);
        account.delete();

        // when & then
        BusinessException exception = assertThrows(BusinessException.class, () -> account.changePhoneNumber("123"));
        assertEquals("Nie można zmienić numeru telefonu usuniętego konta", exception.getMessage());
    }
}