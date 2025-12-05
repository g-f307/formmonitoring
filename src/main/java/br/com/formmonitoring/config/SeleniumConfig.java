package br.com.formmonitoring.config;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.time.Duration;

/**
 * Configuração centralizada para Selenium WebDriver
 */
public class SeleniumConfig {

    private static final String BROWSER = "chrome"; // chrome ou firefox
    private static final boolean HEADLESS = true; // true para modo headless (sem GUI)
    private static final int TIMEOUT_SECONDS = 10;

    /**
     * Obtém uma instância configurada do WebDriver
     */
    public static WebDriver getDriver() {
        WebDriver driver;

        if (BROWSER.equalsIgnoreCase("firefox")) {
            WebDriverManager.firefoxdriver().setup();
            FirefoxOptions options = new FirefoxOptions();
            if (HEADLESS) {
                options.addArguments("--headless");
            }
            driver = new FirefoxDriver(options);
        } else {
            // Chrome é o padrão
            WebDriverManager.chromedriver().setup();
            ChromeOptions options = new ChromeOptions();
            if (HEADLESS) {
                options.addArguments("--headless");
            }
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--disable-gpu");
            options.addArguments("--window-size=1920,1080");
            driver = new ChromeDriver(options);
        }

        // Configurações de timeout
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(TIMEOUT_SECONDS));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(TIMEOUT_SECONDS * 2));
        driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(TIMEOUT_SECONDS));

        return driver;
    }

    /**
     * Fecha o driver de forma segura
     */
    public static void quitDriver(WebDriver driver) {
        if (driver != null) {
            try {
                driver.quit();
            } catch (Exception e) {
                System.err.println("Erro ao fechar driver: " + e.getMessage());
            }
        }
    }
}