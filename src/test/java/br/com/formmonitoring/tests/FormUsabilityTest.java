package br.com.formmonitoring.tests;

import br.com.formmonitoring.config.SeleniumConfig;
import br.com.formmonitoring.model.TestResult;
import br.com.formmonitoring.validators.UsabilityValidator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriver;

import static org.junit.Assert.*;

/**
 * Testes JUnit de Usabilidade
 * Colocar em: src/test/java/br/com/formmonitoring/tests/FormUsabilityTest.java
 */
public class FormUsabilityTest {

    private WebDriver driver;
    private UsabilityValidator validator;
    private String formUrl = "http://localhost:8080/form-monitoring/jsp/form-exemplo.jsp";

    @Before
    public void setUp() {
        driver = SeleniumConfig.getDriver();
        validator = new UsabilityValidator();
        System.out.println("=== Setup: Iniciando teste de Usabilidade ===");
    }

    @Test
    public void testNumeroIdealCampos() {
        System.out.println("Executando: testNumeroIdealCampos");
        TestResult result = validator.validateFieldCount(driver, formUrl);

        System.out.println("  Resultado: " + result.getDetails());
        System.out.println("  Score: " + result.getScore() + "%");

        assertTrue("Número de campos fora do ideal", result.isPassed());
    }

    @Test
    public void testBotaoSubmitVisivel() {
        System.out.println("Executando: testBotaoSubmitVisivel");
        TestResult result = validator.validateSubmitButton(driver, formUrl);

        System.out.println("  Resultado: " + result.getDetails());
        System.out.println("  Score: " + result.getScore() + "%");

        assertTrue("Botão submit não visível", result.isPassed());
    }

    @Test
    public void testMensagensErro() {
        System.out.println("Executando: testMensagensErro");
        TestResult result = validator.validateErrorMessages(driver, formUrl);

        System.out.println("  Resultado: " + result.getDetails());
        System.out.println("  Score: " + result.getScore() + "%");

        assertTrue("Estrutura de mensagens de erro inadequada", result.isPassed());
    }

    @Test
    public void testMascarasEntrada() {
        System.out.println("Executando: testMascarasEntrada");
        TestResult result = validator.validateInputMasks(driver, formUrl);

        System.out.println("  Resultado: " + result.getDetails());
        System.out.println("  Score: " + result.getScore() + "%");

        assertTrue("Máscaras de entrada inadequadas", result.isPassed());
    }

    @Test
    public void testValidacaoEmail() {
        System.out.println("Executando: testValidacaoEmail");
        TestResult result = validator.validateEmailValidation(driver, formUrl);

        System.out.println("  Resultado: " + result.getDetails());
        System.out.println("  Score: " + result.getScore() + "%");

        assertTrue("Validação de email inadequada", result.isPassed());
    }

    @Test
    public void testAgrupamentoLogico() {
        System.out.println("Executando: testAgrupamentoLogico");
        TestResult result = validator.validateFieldGrouping(driver, formUrl);

        System.out.println("  Resultado: " + result.getDetails());
        System.out.println("  Score: " + result.getScore() + "%");

        assertTrue("Agrupamento lógico inadequado", result.isPassed());
    }

    @After
    public void tearDown() {
        SeleniumConfig.quitDriver(driver);
        System.out.println("=== TearDown: Teste finalizado ===\n");
    }
}