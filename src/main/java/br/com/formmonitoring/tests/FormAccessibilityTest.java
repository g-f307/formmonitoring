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
 * Testes de acessibilidade para formulários
 */
public class FormAccessibilityTest {

    private WebDriver driver;
    private TestResultDAO dao;
    private String formUrl = "http://localhost:8080/form-monitoring/jsp/form-exemplo.jsp";
    private long startTime;

    @Before
    public void setUp() {
        driver = SeleniumConfig.getDriver();
        dao = new TestResultDAO();
        startTime = System.currentTimeMillis();
    }

    @Test
    public void testLabelsAssociados() {
        driver.get(formUrl);

        List<WebElement> inputs = driver.findElements(By.cssSelector("input:not([type='submit']):not([type='button'])"));
        int totalInputs = inputs.size();
        int inputsComLabel = 0;

        if (totalInputs == 0) {
            fail("Nenhum input encontrado no formulário");
            return;
        }

        for (WebElement input : inputs) {
            String id = input.getAttribute("id");
            if (id != null && !id.isEmpty()) {
                List<WebElement> labels = driver.findElements(
                        By.cssSelector("label[for='" + id + "']")
                );
                if (!labels.isEmpty()) {
                    inputsComLabel++;
                }
            }
        }

        double percentual = (inputsComLabel * 100.0) / totalInputs;
        boolean passou = percentual >= 90;
        long execTime = System.currentTimeMillis() - startTime;

        TestResult result = new TestResult();
        result.setTestName("Labels Associados");
        result.setCategory("Acessibilidade");
        result.setPassed(passou);
        result.setScore(percentual);
        result.setDetails(String.format("%d/%d inputs possuem labels", inputsComLabel, totalInputs));
        result.setFormUrl(formUrl);
        result.setExecutionTime(execTime);
        result.setTimestamp(new Timestamp(System.currentTimeMillis()));

        dao.save(result);

        assertTrue("Menos de 90% dos inputs possuem labels associados", passou);
    }

    @Test
    public void testCamposObrigatorios() {
        driver.get(formUrl);

        List<WebElement> required = driver.findElements(
                By.cssSelector("input[required], select[required], textarea[required]")
        );

        if (required.isEmpty()) {
            // Se não há campos obrigatórios, teste passa
            saveTestResult("Indicação de Campos Obrigatórios", "Usabilidade", true, 100.0,
                    "Nenhum campo obrigatório encontrado");
            return;
        }

        int camposComIndicador = 0;
        for (WebElement campo : required) {
            WebElement parent = campo.findElement(By.xpath(".."));
            String html = parent.getAttribute("innerHTML");
            String className = parent.getAttribute("class");

            if (html.contains("*") || html.contains("obrigatório") ||
                    html.contains("required") || className.contains("required")) {
                camposComIndicador++;
            }
        }

        double score = (camposComIndicador * 100.0) / required.size();
        boolean passou = score >= 90;

        saveTestResult("Indicação de Campos Obrigatórios", "Usabilidade", passou, score,
                String.format("%d/%d campos obrigatórios indicados visualmente",
                        camposComIndicador, required.size()));

        assertTrue("Campos obrigatórios sem indicação visual adequada", passou);
    }

    @Test
    public void testAtributosARIA() {
        driver.get(formUrl);

        List<WebElement> formElements = driver.findElements(
                By.cssSelector("input, select, textarea, button")
        );

        if (formElements.isEmpty()) {
            fail("Nenhum elemento de formulário encontrado");
            return;
        }

        int elementosComARIA = 0;
        for (WebElement element : formElements) {
            String ariaLabel = element.getAttribute("aria-label");
            String ariaDescribedBy = element.getAttribute("aria-describedby");
            String ariaRequired = element.getAttribute("aria-required");

            if ((ariaLabel != null && !ariaLabel.isEmpty()) ||
                    (ariaDescribedBy != null && !ariaDescribedBy.isEmpty()) ||
                    (ariaRequired != null && !ariaRequired.isEmpty())) {
                elementosComARIA++;
            }
        }

        double score = (elementosComARIA * 100.0) / formElements.size();
        boolean passou = score >= 70; // Critério mais flexível

        saveTestResult("Atributos ARIA", "Acessibilidade", passou, score,
                String.format("%d/%d elementos possuem atributos ARIA",
                        elementosComARIA, formElements.size()));

        assertTrue("Poucos elementos com atributos ARIA", passou);
    }

    @Test
    public void testContrasteElementos() {
        driver.get(formUrl);

        // Este teste é simplificado - em produção usaria ferramenta específica
        List<WebElement> inputs = driver.findElements(By.cssSelector("input, select, textarea"));

        if (inputs.isEmpty()) {
            fail("Nenhum campo de entrada encontrado");
            return;
        }

        int elementosVisiveis = 0;
        for (WebElement input : inputs) {
            if (input.isDisplayed()) {
                elementosVisiveis++;
            }
        }

        double score = (elementosVisiveis * 100.0) / inputs.size();
        boolean passou = score >= 95;

        saveTestResult("Visibilidade de Elementos", "Acessibilidade", passou, score,
                String.format("%d/%d elementos visíveis", elementosVisiveis, inputs.size()));

        assertTrue("Elementos com baixa visibilidade detectados", passou);
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