package com.example.springboot_backend.system.e2e;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;

public class RegisterSystemTest {
    static Playwright playwright;
    static Browser browser;

    @BeforeAll
    static void setup() {
        playwright = Playwright.create();
        browser = playwright.firefox().launch(new BrowserType.LaunchOptions().setHeadless(false));
    }

    @Test
    void shouldDisplaySuccessMessageAfterRegistering() {
        Page page = browser.newPage();

        page.navigate("http://localhost:5173/register");

        page.fill("input[id='adres-e-mail']", "test3@mail.com");
        page.fill("input[id='hasło']", "Test1234");
        page.click("button[type='submit']");

        page.click("text='Przejdź do potwierdzenia e-maila'");
        page.click("button[type='submit']");

        Locator alert = page.locator(".form-success");
        Assertions.assertTrue(alert.isVisible());
        Assertions.assertEquals("Adres e-mail został potwierdzony. Konto jest aktywne.", alert.textContent());
    }

    @AfterAll
    static void tearDown() {
        playwright.close();
    }
}