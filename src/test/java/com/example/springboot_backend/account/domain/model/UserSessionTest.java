package com.example.springboot_backend.account.domain.model;

import com.example.springboot_backend.account.domain.valueobject.AccessToken;
import com.example.springboot_backend.account.domain.valueobject.UserAccountId;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

class UserSessionTest {

    @Test
    void shouldCreateNewSession() {
        // given
        UserAccountId accountId = UserAccountId.newId();
        AccessToken token = new AccessToken("token-123", Instant.now().plus(1, ChronoUnit.HOURS));

        // when
        UserSession session = UserSession.create(accountId, token);

        // then
        assertNotNull(session.id());
        assertEquals(accountId, session.accountId());
        assertEquals(token, session.token());
        assertNotNull(session.createdAt());
        assertEquals(SessionStatus.ACTIVE, session.status());
        assertTrue(session.valid());
    }

    @Test
    void shouldInvalidateSession() {
        // given
        UserSession session = UserSession.create(UserAccountId.newId(), new AccessToken("val", Instant.now().plus(1, ChronoUnit.HOURS)));

        // when
        session.invalidate();

        // then
        assertEquals(SessionStatus.INVALIDATED, session.status());
        assertFalse(session.valid());
    }

    @Test
    void shouldBeExpiredWhenTokenIsExpired() {
        // given
        AccessToken expiredToken = new AccessToken("expired", Instant.now().minus(1, ChronoUnit.MINUTES));
        UserSession session = UserSession.create(UserAccountId.newId(), expiredToken);

        // then
        assertTrue(session.expired());
        assertFalse(session.valid());
        assertEquals(SessionStatus.EXPIRED, session.status());
    }

    @Test
    void shouldNotBeValidWhenInvalidatedEvenIfTokenNotExpired() {
        // given
        UserSession session = UserSession.create(UserAccountId.newId(), new AccessToken("val", Instant.now().plus(1, ChronoUnit.HOURS)));

        // when
        session.invalidate();

        // then
        assertFalse(session.valid());
        assertEquals(SessionStatus.INVALIDATED, session.status());
    }

    @Test
    void statusShouldReturnExpiredWhenTokenExpiredEvenIfStatusWasActive() {
        // given
        AccessToken token = new AccessToken("val", Instant.now().minus(1, ChronoUnit.SECONDS));
        UserSession session = UserSession.create(UserAccountId.newId(), token);

        // then
        assertEquals(SessionStatus.EXPIRED, session.status());
    }
}