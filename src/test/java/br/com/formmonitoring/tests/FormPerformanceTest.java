package br.com.formmonitoring.tests;

import br.com.formmonitoring.config.SeleniumConfig;
import br.com.formmonitoring.model.TestResult;
import br.com.formmonitoring.validators.PerformanceValidator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriver;

import static org.junit.Assert.*;

/**
 * Testes JUnit de Performance
 * Colocar em: src/test/java/br/com/formmonitoring/tests/FormPerformanceTest.java
 */
public class FormPerformanceTest {

    private WebDriver driver;
    private PerformanceValidator validator;
    private String formUrl = "http://localhost:8080/form-monitoring/jsp/form-exemplo.jsp";

    @Before
    public void setUp() {
        driver = SeleniumConfig.getDriver();
        validator = new PerformanceValidator();
        System.out.println("=== Setup: Iniciando teste de Performance ===");
    }

    @Test
    public void testTempoCarregamento() {
        System.out.println("Executando: testTempoCarregamento");
        TestResult result = validator.validateLoadingTime(driver, formUrl);

        System.out.println("  Resultado: " + result.getDetails());
        System.out.println("  Score: " + result.getScore() + "%");

        assertTrue("Tempo de carregamento acima do limite", result.isPassed());
    }

    @Test
    public void testResponsividadeMobile() {
        System.out.println("Executando: testResponsividadeMobile");
        TestResult result = validator.validateMobileResponsiveness(driver, formUrl);

        System.out.println("  Resultado: " + result.getDetails());
        System.out.println("  Score: " + result.getScore() + "%");

        assertTrue("Formulário não responsivo em mobile", result.isPassed());
    }

    @Test
    public void testResponsividadeTablet() {
        System.out.println("Executando: testResponsividadeTablet");
        TestResult result = validator.validateTabletResponsiveness(driver, formUrl);

        System.out.println("  Resultado: " + result.getDetails());
        System.out.println("  Score: " + result.getScore() + "%");

        assertTrue("Formulário não responsivo em tablet", result.isPassed());
    }

    @Test
    public void testTamanhoFonte() {
        System.out.println("Executando: testTamanhoFonte");
        TestResult result = validator.validateFontSize(driver, formUrl);

        System.out.println("  Resultado: " + result.getDetails());
        System.out.println("  Score: " + result.getScore() + "%");

        assertTrue("Fontes muito pequenas detectadas", result.isPassed());
    }

    @Test
    public void testEspacamentoEntreCampos() {
        System.out.println("Executando: testEspacamentoEntreCampos");
        TestResult result = validator.validateFieldSpacing(driver, formUrl);

        System.out.println("  Resultado: " + result.getDetails());
        System.out.println("  Score: " + result.getScore() + "%");

        assertTrue("Espaçamento inadequado entre campos", result.isPassed());
    }

    @After
    public void tearDown() {
        SeleniumConfig.quitDriver(driver);
        System.out.println("=== TearDown: Teste finalizado ===\n");
    }
}