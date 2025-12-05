package br.com.formmonitoring.tests;

import br.com.formmonitoring.config.SeleniumConfig;
import br.com.formmonitoring.model.TestResult;
import br.com.formmonitoring.validators.AccessibilityValidator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriver;

import static org.junit.Assert.*;

/**
 * Testes JUnit de Acessibilidade
 * Colocar em: src/test/java/br/com/formmonitoring/tests/FormAccessibilityTest.java
 */
public class FormAccessibilityTest {

    private WebDriver driver;
    private AccessibilityValidator validator;
    private String formUrl = "http://localhost:8080/form-monitoring/jsp/form-exemplo.jsp";

    @Before
    public void setUp() {
        driver = SeleniumConfig.getDriver();
        validator = new AccessibilityValidator();
        System.out.println("=== Setup: Iniciando teste de Acessibilidade ===");
    }

    @Test
    public void testLabelsAssociados() {
        System.out.println("Executando: testLabelsAssociados");
        TestResult result = validator.validateAssociatedLabels(driver, formUrl);

        System.out.println("  Resultado: " + result.getDetails());
        System.out.println("  Score: " + result.getScore() + "%");

        assertTrue("Menos de 90% dos inputs possuem labels associados", result.isPassed());
    }

    @Test
    public void testCamposObrigatorios() {
        System.out.println("Executando: testCamposObrigatorios");
        TestResult result = validator.validateRequiredFieldsIndication(driver, formUrl);

        System.out.println("  Resultado: " + result.getDetails());
        System.out.println("  Score: " + result.getScore() + "%");

        assertTrue("Campos obrigatórios sem indicação visual adequada", result.isPassed());
    }

    @Test
    public void testAtributosARIA() {
        System.out.println("Executando: testAtributosARIA");
        TestResult result = validator.validateARIAAttributes(driver, formUrl);

        System.out.println("  Resultado: " + result.getDetails());
        System.out.println("  Score: " + result.getScore() + "%");

        assertTrue("Poucos elementos com atributos ARIA", result.isPassed());
    }

    @Test
    public void testContrasteElementos() {
        System.out.println("Executando: testContrasteElementos");
        TestResult result = validator.validateElementVisibility(driver, formUrl);

        System.out.println("  Resultado: " + result.getDetails());
        System.out.println("  Score: " + result.getScore() + "%");

        assertTrue("Elementos com baixa visibilidade detectados", result.isPassed());
    }

    @After
    public void tearDown() {
        SeleniumConfig.quitDriver(driver);
        System.out.println("=== TearDown: Teste finalizado ===\n");
    }
}