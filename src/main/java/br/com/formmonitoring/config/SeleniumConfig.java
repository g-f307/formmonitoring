package br.com.formmonitoring.config;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.GeckoDriverService;

import java.io.File;
import java.time.Duration;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Configuração otimizada do Selenium WebDriver
 * Versão melhorada para reduzir warnings
 */
public class SeleniumConfig {

    private static final String BROWSER = "firefox";
    private static final boolean HEADLESS = true;
    private static final int TIMEOUT_SECONDS = 10;

    // Suprime logs do Selenium
    static {
        Logger.getLogger("org.openqa.selenium").setLevel(Level.SEVERE);
        Logger.getLogger("org.openqa.selenium.os.ExternalProcess").setLevel(Level.OFF);
        System.setProperty("webdriver.firefox.logfile", "/dev/null"); // Linux
        // System.setProperty("webdriver.firefox.logfile", "NUL"); // Windows
    }

    /**
     * Obtém uma instância configurada do WebDriver
     */
    public static WebDriver getDriver() {
        WebDriver driver = null;

        try {
            if (BROWSER.equalsIgnoreCase("firefox")) {
                System.out.println("🦊 Inicializando Firefox WebDriver...");

                WebDriverManager.firefoxdriver().setup();

                // Configurações do serviço para reduzir logs
                GeckoDriverService.Builder serviceBuilder = new GeckoDriverService.Builder()
                        .withLogOutput(new java.io.OutputStream() {
                            @Override
                            public void write(int b) {
                                // Descarta logs do geckodriver
                            }
                        });

                FirefoxOptions options = new FirefoxOptions();

                if (HEADLESS) {
                    options.addArguments("--headless");
                    System.out.println("   ✓ Modo headless ativado");
                }

                // Opções para reduzir warnings e melhorar performance
                options.addArguments("--no-sandbox");
                options.addArguments("--disable-dev-shm-usage");
                options.addArguments("--disable-gpu");
                options.setPageLoadStrategy(org.openqa.selenium.PageLoadStrategy.NORMAL);

                // Suprime logs do Firefox
                options.addPreference("devtools.console.stdout.content", false);

                driver = new FirefoxDriver(serviceBuilder.build(), options);
                System.out.println("   ✓ Firefox iniciado com sucesso!");

            } else {
                // Chrome (fallback)
                System.out.println("🔷 Inicializando Chrome WebDriver...");

                WebDriverManager.chromedriver().setup();
                ChromeOptions options = new ChromeOptions();

                if (HEADLESS) {
                    options.addArguments("--headless=new"); // Nova sintaxe do Chrome
                }

                options.addArguments("--no-sandbox");
                options.addArguments("--disable-dev-shm-usage");
                options.addArguments("--disable-gpu");
                options.addArguments("--window-size=1920,1080");
                options.addArguments("--disable-logging");
                options.addArguments("--log-level=3");
                options.addArguments("--silent");

                // Suprime logs do Chrome
                options.setExperimentalOption("excludeSwitches", new String[]{"enable-logging"});

                driver = new ChromeDriver(options);
                System.out.println("   ✓ Chrome iniciado com sucesso!");
            }

            // Configurações de timeout
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
        }

        return driver;
    }

    /**
     * Fecha o driver de forma segura e silenciosa
     */
    public static void quitDriver(WebDriver driver) {
        if (driver != null) {
            try {
                driver.quit();
                System.out.println("✓ WebDriver fechado com sucesso");
            } catch (Exception e) {
                // Ignora erros ao fechar (comum com processos já encerrados)
            }
        }
    }

    /**
     * Método para testar se o WebDriver funciona
     */
    public static boolean testWebDriver() {
        System.out.println("\n🧪 TESTANDO WEBDRIVER...\n");

        WebDriver driver = null;
        try {
            driver = getDriver();

            if (driver == null) {
                System.err.println("❌ Driver retornou null");
                return false;
            }

            System.out.println("✅ WebDriver iniciado com sucesso!");

            driver.get("https://www.example.com");
            System.out.println("✅ Navegação funcionando!");

            String title = driver.getTitle();
            System.out.println("✅ Título da página: " + title);

            return true;

        } catch (Exception e) {
            System.err.println("❌ Erro no teste: " + e.getMessage());
            return false;

        } finally {
            quitDriver(driver);
        }
    }

    public static void main(String[] args) {
        boolean success = testWebDriver();
        System.out.println(success ? "\n🎉 TUDO FUNCIONANDO!" : "\n❌ TESTE FALHOU");
    }
}