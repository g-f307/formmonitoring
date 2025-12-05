package br.com.formmonitoring.tests;

import br.com.formmonitoring.config.SeleniumConfig;
import br.com.formmonitoring.dao.TestResultDAO;
import br.com.formmonitoring.model.TestResult;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.sql.Timestamp;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Testes de usabilidade para formulários
 */
public class FormUsabilityTest {

    private WebDriver driver;
    private TestResultDAO dao;
    private String formUrl = "http://localhost:8080/form-monitoring/form-exemplo.jsp";
    private long startTime;

    @Before
    public void setUp() {
        driver = SeleniumConfig.getDriver();
        dao = new TestResultDAO();
        startTime = System.currentTimeMillis();
    }

    @Test
    public void testNumeroIdealCampos() {
        driver.get(formUrl);

        List<WebElement> campos = driver.findElements(
                By.cssSelector("input:not([type='submit']):not([type='button']):not([type='hidden']), select, textarea")
        );

        int totalCampos = campos.size();
        boolean passou = totalCampos >= 3 && totalCampos <= 10;

        // Score baseado em proximidade do ideal (5-7 campos)
        double score;
        if (totalCampos >= 5 && totalCampos <= 7) {
            score = 100.0;
        } else if (totalCampos >= 3 && totalCampos <= 10) {
            score = 80.0;
        } else if (totalCampos > 10) {
            score = Math.max(50.0, 100.0 - (totalCampos - 10) * 5);
        } else {
            score = 60.0;
        }

        saveTestResult("Número Ideal de Campos", "Usabilidade", passou, score,
                String.format("Formulário possui %d campos (ideal: 5-7)", totalCampos));

        assertTrue("Número de campos fora do ideal", passou);
    }

    @Test
    public void testMensagensErro() {
        driver.get(formUrl);

        // Procura por elementos que indiquem feedback de erro
        List<WebElement> errorMessages = driver.findElements(
                By.cssSelector(".error, .error-message, .invalid-feedback, [class*='error']")
        );

        List<WebElement> campos = driver.findElements(
                By.cssSelector("input:not([type='submit']):not([type='button']), select, textarea")
        );

        boolean temEstruturaMensagens = !errorMessages.isEmpty() || campos.isEmpty();
        double score = temEstruturaMensagens ? 100.0 : 50.0;

        saveTestResult("Estrutura de Mensagens de Erro", "Usabilidade", temEstruturaMensagens, score,
                String.format("Encontrados %d containers para mensagens de erro", errorMessages.size()));

        assertTrue("Estrutura de mensagens de erro não encontrada", temEstruturaMensagens);
    }

    @Test
    public void testBotaoSubmitVisivel() {
        driver.get(formUrl);

        List<WebElement> submitButtons = driver.findElements(
                By.cssSelector("input[type='submit'], button[type='submit']")
        );

        boolean passou = false;
        int botoesVisiveis = 0;

        for (WebElement button : submitButtons) {
            if (button.isDisplayed()) {
                botoesVisiveis++;
                passou = true;
            }
        }

        double score = passou ? 100.0 : 0.0;

        saveTestResult("Botão Submit Visível", "Usabilidade", passou, score,
                String.format("%d botões de submit visíveis encontrados", botoesVisiveis));

        assertTrue("Nenhum botão de submit visível", passou);
    }

    @Test
    public void testValidacaoEmail() {
        driver.get(formUrl);

        List<WebElement> emailInputs = driver.findElements(
                By.cssSelector("input[type='email']")
        );

        int emailsComPattern = 0;
        for (WebElement email : emailInputs) {
            String pattern = email.getAttribute("pattern");
            if (pattern != null && !pattern.isEmpty()) {
                emailsComPattern++;
            }
        }

        double score = emailInputs.isEmpty() ? 100.0 :
                (emailsComPattern * 100.0) / emailInputs.size();
        boolean passou = score >= 80 || emailInputs.isEmpty();

        saveTestResult("Validação de Email", "Validação", passou, score,
                String.format("%d/%d campos email com validação pattern",
                        emailsComPattern, emailInputs.size()));
    }

    @Test
    public void testMascarasEntrada() {
        driver.get(formUrl);

        // Verifica se há campos com máscaras (placeholder ou pattern)
        List<WebElement> camposComMascara = driver.findElements(
                By.cssSelector("input[placeholder], input[pattern], input[maxlength]")
        );

        List<WebElement> todosCampos = driver.findElements(
                By.cssSelector("input[type='text'], input[type='tel'], input[type='number']")
        );

        if (todosCampos.isEmpty()) {
            saveTestResult("Máscaras de Entrada", "Usabilidade", true, 100.0,
                    "Nenhum campo que necessite máscara");
            return;
        }

        double score = (camposComMascara.size() * 100.0) / todosCampos.size();
        boolean passou = score >= 60;

        saveTestResult("Máscaras de Entrada", "Usabilidade", passou, score,
                String.format("%d/%d campos com máscaras ou validação",
                        camposComMascara.size(), todosCampos.size()));
    }

    @Test
    public void testAgrupamentoLogico() {
        driver.get(formUrl);

        // Verifica se há fieldsets ou divs agrupando campos relacionados
        List<WebElement> fieldsets = driver.findElements(By.tagName("fieldset"));
        List<WebElement> sections = driver.findElements(
                By.cssSelector("div[class*='group'], div[class*='section'], .form-group")
        );

        int gruposEncontrados = fieldsets.size() + sections.size();
        boolean passou = gruposEncontrados > 0;
        double score = passou ? Math.min(100.0, gruposEncontrados * 25) : 50.0;

        saveTestResult("Agrupamento Lógico de Campos", "Design", passou, score,
                String.format("Encontrados %d agrupamentos de campos", gruposEncontrados));
    }

    /**
     * Método auxiliar para salvar resultados
     */
    private void saveTestResult(String testName, String category, boolean passed,
                                double score, String details) {
        long execTime = System.currentTimeMillis() - startTime;

        TestResult result = new TestResult();
        result.setTestName(testName);
        result.setCategory(category);
        result.setPassed(passed);
        result.setScore(score);
        result.setDetails(details);
        result.setFormUrl(formUrl);
        result.setExecutionTime(execTime);
        result.setTimestamp(new Timestamp(System.currentTimeMillis()));

        dao.save(result);
    }

    @After
    public void tearDown() {
        SeleniumConfig.quitDriver(driver);
    }
}