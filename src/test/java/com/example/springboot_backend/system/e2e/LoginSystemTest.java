package com.example.springboot_backend.system.e2e;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class LoginSystemTest {
    static Playwright playwright;
    static Browser browser;

    @BeforeAll
    static void setup() {
        playwright = Playwright.create();
        browser = playwright.firefox().launch(new BrowserType.LaunchOptions().setHeadless(false));
    }

    @Test
    void shouldShowAccountPageAfterLogin() {
        Page page = browser.newPage();

        page.navigate("http://localhost:5173/login");

        page.fill("input[id='adres-e-mail']", "test@mail.com");
        page.fill("input[id='hasło']", "Test1234");
        page.click("button[type='submit']");

        assertThat(page).hasURL("http://localhost:5173/account");

        Locator alert = page.locator("text='Wyloguj'");
        Assertions.assertTrue(alert.isVisible());
    }

    @AfterAll
    static void tearDown() {
        playwright.close();
    }
}