package com.example.springboot_backend.account.domain.service;

import com.example.springboot_backend.account.domain.valueobject.EmailAddress;
import com.example.springboot_backend.shared.exception.BusinessException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordPolicyTest {

    private final PasswordPolicy passwordPolicy = new PasswordPolicy();

    @Test
    void shouldPassWhenPasswordIsValid() {
        // given
        String validPassword = "Password1";
        EmailAddress email = new EmailAddress("user@example.com");

        // when & then
        assertDoesNotThrow(() -> passwordPolicy.check(validPassword, email));
    }

    @Test
    void shouldThrowWhenPasswordIsNull() {
        // given
        String password = null;
        EmailAddress email = new EmailAddress("user@example.com");

        // when & then
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> passwordPolicy.check(password, email));
        assertEquals("Hasło nie może być puste", exception.getMessage());
    }

    @Test
    void shouldThrowWhenPasswordIsBlank() {
        // given
        String password = "   ";
        EmailAddress email = new EmailAddress("user@example.com");

        // when & then
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> passwordPolicy.check(password, email));
        assertEquals("Hasło nie może być puste", exception.getMessage());
    }

    @Test
    void shouldThrowWhenPasswordIsTooShort() {
        // given
        String password = "Pass1";
        EmailAddress email = new EmailAddress("user@example.com");

        // when & then
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> passwordPolicy.check(password, email));
        assertEquals("Hasło musi mieć co najmniej 8 znaków", exception.getMessage());
    }

    @Test
    void shouldThrowWhenPasswordHasNoLetters() {
        // given
        String password = "12345678";
        EmailAddress email = new EmailAddress("user@example.com");

        // when & then
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> passwordPolicy.check(password, email));
        assertEquals("Hasło musi zawierać literę", exception.getMessage());
    }

    @Test
    void shouldThrowWhenPasswordHasNoDigits() {
        // given
        String password = "password";
        EmailAddress email = new EmailAddress("user@example.com");

        // when & then
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> passwordPolicy.check(password, email));
        assertEquals("Hasło musi zawierać cyfrę", exception.getMessage());
    }

    @Test
    void shouldThrowWhenPasswordIsSameAsEmail() {
        // given
        String password = "User1@example.com";
        EmailAddress email = new EmailAddress("User1@example.com");

        // when & then
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> passwordPolicy.check(password, email));
        assertEquals("Hasło nie może być takie samo jak e-mail", exception.getMessage());
    }

    @Test
    void shouldThrowWhenPasswordIsSameAsEmailIgnoreCase() {
        // given
        String password = "USER1@EXAMPLE.COM";
        EmailAddress email = new EmailAddress("user1@example.com");

        // when & then
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> passwordPolicy.check(password, email));
        assertEquals("Hasło nie może być takie samo jak e-mail", exception.getMessage());
    }

    @Test
    void shouldPassWhenEmailIsNull() {
        // given
        String validPassword = "Password1";
        EmailAddress email = null;

        // when & then
        assertDoesNotThrow(() -> passwordPolicy.check(validPassword, email));
    }
}