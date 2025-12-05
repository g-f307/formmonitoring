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
 * VERSÃO CORRIGIDA PARA LINUX/FEDORA
 */
public class SeleniumConfig {

    // MUDADO PARA FIREFOX (mais compatível com Linux)
    private static final String BROWSER = "firefox"; // chrome ou firefox
    private static final boolean HEADLESS = true; // true para modo headless (sem GUI)
    private static final int TIMEOUT_SECONDS = 10;

    /**
     * Obtém uma instância configurada do WebDriver
     */
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
                }

                // Opções adicionais para Linux
                options.addArguments("--no-sandbox");
                options.addArguments("--disable-dev-shm-usage");

                driver = new FirefoxDriver(options);
                System.out.println("   ✓ Firefox iniciado com sucesso!");

            } else {
                // Chrome (fallback)
                System.out.println("🔷 Inicializando Chrome WebDriver...");

                WebDriverManager.chromedriver().setup();
                ChromeOptions options = new ChromeOptions();

                if (HEADLESS) {
                    options.addArguments("--headless");
                }
                options.addArguments("--no-sandbox");
                options.addArguments("--disable-dev-shm-usage");
                options.addArguments("--disable-gpu");
                options.addArguments("--window-size=1920,1080");

                // Para Linux, tenta localizar o Chrome em locais comuns
                options.setBinary("/usr/bin/google-chrome");

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
            System.err.println("   Headless: " + HEADLESS);
            System.err.println("   Erro: " + e.getMessage());

            // Sugestões de solução
            System.err.println("\n💡 SUGESTÕES:");
            if (BROWSER.equalsIgnoreCase("chrome")) {
                System.err.println("   1. Instale o Chrome: sudo dnf install google-chrome-stable");
                System.err.println("   2. Ou mude para Firefox na linha 18: private static final String BROWSER = \"firefox\";");
            } else {
                System.err.println("   1. Instale o Firefox: sudo dnf install firefox");
                System.err.println("   2. Verifique se está instalado: which firefox");
            }

            e.printStackTrace();
        }

        return driver;
    }

    /**
     * Fecha o driver de forma segura
     */
    public static void quitDriver(WebDriver driver) {
        if (driver != null) {
            try {
                driver.quit();
                System.out.println("✓ WebDriver fechado com sucesso");
            } catch (Exception e) {
                System.err.println("Erro ao fechar driver: " + e.getMessage());
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

            // Testa navegação
            driver.get("https://www.example.com");
            System.out.println("✅ Navegação funcionando!");

            String title = driver.getTitle();
            System.out.println("✅ Título da página: " + title);

            return true;

        } catch (Exception e) {
            System.err.println("❌ Erro no teste: " + e.getMessage());
            e.printStackTrace();
            return false;

        } finally {
            quitDriver(driver);
        }
    }

    /**
     * Main para testar a configuração
     */
    public static void main(String[] args) {
        boolean success = testWebDriver();

        if (success) {
            System.out.println("\n🎉 TUDO FUNCIONANDO!");
        } else {
            System.err.println("\n❌ TESTE FALHOU - Verifique os erros acima");
        }
    }
}