package br.com.formmonitoring.config;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.time.Duration;

public class SeleniumConfig {

    private static final String BROWSER = "firefox";

    private static final boolean HEADLESS = false;

    private static final int TIMEOUT_SECONDS = 10;

    public static WebDriver getDriver() {
        WebDriver driver = null;

        try {
            if (BROWSER.equalsIgnoreCase("firefox")) {
                System.out.println("🦊 Inicializando Firefox WebDriver...");
                WebDriverManager.firefoxdriver().setup();

                FirefoxOptions options = new FirefoxOptions();

                if (HEADLESS) {
                    options.addArguments("--headless");
                    System.out.println("   ✓ Modo headless ativado");
                } else {
                    System.out.println("   👀 Modo VISUAL ativado - você verá o navegador");
                }

                options.addArguments("--no-sandbox");
                options.addArguments("--disable-dev-shm-usage");
                options.setPageLoadStrategy(org.openqa.selenium.PageLoadStrategy.NORMAL);

                driver = new FirefoxDriver(options);

                driver.manage().window().maximize();

                System.out.println("   ✓ Firefox iniciado com sucesso!");

            } else {
                System.out.println("🔷 Inicializando Chrome WebDriver...");
                WebDriverManager.chromedriver().setup();

                ChromeOptions options = new ChromeOptions();

                if (HEADLESS) {
                    options.addArguments("--headless=new");
                } else {
                    System.out.println("   👀 Modo VISUAL ativado - você verá o navegador");
                }

                options.addArguments("--no-sandbox");
                options.addArguments("--disable-dev-shm-usage");
                options.addArguments("--window-size=1920,1080");

                driver = new ChromeDriver(options);
                driver.manage().window().maximize();

                System.out.println("   ✓ Chrome iniciado com sucesso!");
            }

            if (driver != null) {
                driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(TIMEOUT_SECONDS));
                driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(TIMEOUT_SECONDS * 2));
                driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(TIMEOUT_SECONDS));
                System.out.println("   ✓ Timeouts configurados");
            }

        } catch (Exception e) {
            System.err.println("❌ ERRO ao inicializar WebDriver:");
            System.err.println("   Browser: " + BROWSER);
            System.err.println("   Erro: " + e.getMessage());
            e.printStackTrace();
        }

        return driver;
    }

    public static void quitDriver(WebDriver driver) {
        if (driver != null) {
            try {
                driver.quit();
                System.out.println("✓ WebDriver fechado com sucesso");
            } catch (Exception e) {
            }
        }
    }
}