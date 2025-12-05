package br.com.formmonitoring.tests;

import br.com.formmonitoring.config.SeleniumConfig;
import br.com.formmonitoring.dao.TestResultDAO;
import br.com.formmonitoring.model.TestResult;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.sql.Timestamp;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Testes de performance e responsividade para formulários
 */
public class FormPerformanceTest {

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
    public void testTempoCarregamento() {
        long inicio = System.currentTimeMillis();
        driver.get(formUrl);

        // Aguarda até que o body esteja presente
        driver.findElement(By.tagName("body"));

        long tempoCarregamento = System.currentTimeMillis() - inicio;
        boolean passou = tempoCarregamento <= 3000; // 3 segundos

        double score = 100.0;
        if (tempoCarregamento > 3000) {
            score = Math.max(50.0, 100.0 - ((tempoCarregamento - 3000) / 100.0));
        }

        saveTestResult("Tempo de Carregamento", "Performance", passou, score,
                String.format("Página carregou em %d ms (limite: 3000ms)", tempoCarregamento));

        assertTrue("Tempo de carregamento acima do limite", passou);
    }

    @Test
    public void testResponsividadeMobile() {
        // Testa em resolução mobile (375x667 - iPhone SE)
        driver.manage().window().setSize(new Dimension(375, 667));
        driver.get(formUrl);

        List<WebElement> campos = driver.findElements(
                By.cssSelector("input, select, textarea")
        );

        int camposVisiveis = 0;
        int camposClicaveis = 0;

        for (WebElement campo : campos) {
            if (campo.isDisplayed()) {
                camposVisiveis++;
                if (campo.isEnabled()) {
                    camposClicaveis++;
                }
            }
        }

        boolean passou = camposVisiveis > 0 && camposClicaveis == camposVisiveis;
        double score = campos.isEmpty() ? 100.0 : (camposClicaveis * 100.0) / campos.size();

        saveTestResult("Responsividade Mobile", "Design", passou, score,
                String.format("%d/%d campos visíveis e clicáveis em mobile",
                        camposClicaveis, campos.size()));

        // Restaura tamanho normal
        driver.manage().window().setSize(new Dimension(1920, 1080));

        assertTrue("Formulário não responsivo em mobile", passou);
    }

    @Test
    public void testResponsividadeTablet() {
        // Testa em resolução tablet (768x1024 - iPad)
        driver.manage().window().setSize(new Dimension(768, 1024));
        driver.get(formUrl);

        List<WebElement> campos = driver.findElements(
                By.cssSelector("input, select, textarea")
        );

        int camposVisiveis = 0;
        for (WebElement campo : campos) {
            if (campo.isDisplayed()) {
                camposVisiveis++;
            }
        }

        boolean passou = camposVisiveis == campos.size();
        double score = campos.isEmpty() ? 100.0 : (camposVisiveis * 100.0) / campos.size();

        saveTestResult("Responsividade Tablet", "Design", passou, score,
                String.format("%d/%d campos visíveis em tablet", camposVisiveis, campos.size()));

        // Restaura tamanho normal
        driver.manage().window().setSize(new Dimension(1920, 1080));

        assertTrue("Formulário não responsivo em tablet", passou);
    }

    @Test
    public void testTamanhoFonte() {
        driver.get(formUrl);

        List<WebElement> labels = driver.findElements(By.tagName("label"));
        List<WebElement> inputs = driver.findElements(
                By.cssSelector("input, select, textarea")
        );

        int elementosComFonteAdequada = 0;
        int totalElementos = labels.size() + inputs.size();

        // Verifica labels
        for (WebElement label : labels) {
            String fontSize = label.getCssValue("font-size");
            if (fontSize != null && !fontSize.isEmpty()) {
                double size = Double.parseDouble(fontSize.replace("px", ""));
                if (size >= 14) { // Mínimo 14px
                    elementosComFonteAdequada++;
                }
            }
        }

        // Verifica inputs
        for (WebElement input : inputs) {
            String fontSize = input.getCssValue("font-size");
            if (fontSize != null && !fontSize.isEmpty()) {
                double size = Double.parseDouble(fontSize.replace("px", ""));
                if (size >= 14) {
                    elementosComFonteAdequada++;
                }
            }
        }

        if (totalElementos == 0) {
            saveTestResult("Tamanho de Fonte", "Design", true, 100.0,
                    "Nenhum elemento de texto encontrado");
            return;
        }

        double score = (elementosComFonteAdequada * 100.0) / totalElementos;
        boolean passou = score >= 90;

        saveTestResult("Tamanho de Fonte", "Design", passou, score,
                String.format("%d/%d elementos com fonte >= 14px",
                        elementosComFonteAdequada, totalElementos));

        assertTrue("Fontes muito pequenas detectadas", passou);
    }

    @Test
    public void testEspacamentoEntreCampos() {
        driver.get(formUrl);

        List<WebElement> campos = driver.findElements(
                By.cssSelector("input, select, textarea")
        );

        if (campos.size() < 2) {
            saveTestResult("Espaçamento Entre Campos", "Design", true, 100.0,
                    "Menos de 2 campos - espaçamento não aplicável");
            return;
        }

        int camposComEspacamento = 0;

        for (WebElement campo : campos) {
            String marginBottom = campo.getCssValue("margin-bottom");
            String paddingBottom = campo.getCssValue("padding-bottom");

            // Verifica se o container pai tem espaçamento
            WebElement parent = campo.findElement(By.xpath(".."));
            String parentMargin = parent.getCssValue("margin-bottom");
            String parentPadding = parent.getCssValue("padding-bottom");

            if ((marginBottom != null && !marginBottom.equals("0px")) ||
                    (paddingBottom != null && !paddingBottom.equals("0px")) ||
                    (parentMargin != null && !parentMargin.equals("0px")) ||
                    (parentPadding != null && !parentPadding.equals("0px"))) {
                camposComEspacamento++;
            }
        }

        double score = (camposComEspacamento * 100.0) / campos.size();
        boolean passou = score >= 70;

        saveTestResult("Espaçamento Entre Campos", "Design", passou, score,
                String.format("%d/%d campos com espaçamento adequado",
                        camposComEspacamento, campos.size()));

        assertTrue("Espaçamento inadequado entre campos", passou);
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