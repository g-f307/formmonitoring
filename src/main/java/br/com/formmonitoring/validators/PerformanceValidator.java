package br.com.formmonitoring.validators;

import br.com.formmonitoring.model.TestResult;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.sql.Timestamp;
import java.util.List;

/**
 * Validador de Performance e Responsividade de Formulários
 */
public class PerformanceValidator {

    /**
     * Valida tempo de carregamento da página
     */
    public TestResult validateLoadingTime(WebDriver driver, String formUrl) {
        long inicio = System.currentTimeMillis();

        try {
            driver.get(formUrl);
            driver.findElement(By.tagName("body")); // Aguarda body estar presente

            long tempoCarregamento = System.currentTimeMillis() - inicio;
            boolean passou = tempoCarregamento <= 3000;

            double score = 100.0;
            if (tempoCarregamento > 3000) {
                score = Math.max(50.0, 100.0 - ((tempoCarregamento - 3000) / 100.0));
            }

            return createTestResult(
                    "Tempo de Carregamento",
                    "Performance",
                    passou,
                    score,
                    String.format("Página carregou em %d ms (limite: 3000ms)", tempoCarregamento),
                    formUrl,
                    tempoCarregamento
            );

        } catch (Exception e) {
            return createErrorResult("Tempo de Carregamento", "Performance", formUrl, e);
        }
    }

    /**
     * Valida responsividade em resolução mobile
     */
    public TestResult validateMobileResponsiveness(WebDriver driver, String formUrl) {
        long startTime = System.currentTimeMillis();

        try {
            // Testa em resolução mobile (375x667 - iPhone SE)
            driver.manage().window().setSize(new Dimension(375, 667));
            driver.get(formUrl);
            Thread.sleep(1000);

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

            // Restaura tamanho normal
            driver.manage().window().setSize(new Dimension(1920, 1080));

            return createTestResult(
                    "Responsividade Mobile",
                    "Design",
                    passou,
                    score,
                    String.format("%d/%d campos visíveis e clicáveis em mobile",
                            camposClicaveis, campos.size()),
                    formUrl,
                    System.currentTimeMillis() - startTime
            );

        } catch (Exception e) {
            // Restaura tamanho em caso de erro
            try {
                driver.manage().window().setSize(new Dimension(1920, 1080));
            } catch (Exception ignored) {}

            return createErrorResult("Responsividade Mobile", "Design", formUrl, e);
        }
    }

    /**
     * Valida responsividade em resolução tablet
     */
    public TestResult validateTabletResponsiveness(WebDriver driver, String formUrl) {
        long startTime = System.currentTimeMillis();

        try {
            // Testa em resolução tablet (768x1024 - iPad)
            driver.manage().window().setSize(new Dimension(768, 1024));
            driver.get(formUrl);
            Thread.sleep(1000);

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

            // Restaura tamanho normal
            driver.manage().window().setSize(new Dimension(1920, 1080));

            return createTestResult(
                    "Responsividade Tablet",
                    "Design",
                    passou,
                    score,
                    String.format("%d/%d campos visíveis em tablet", camposVisiveis, campos.size()),
                    formUrl,
                    System.currentTimeMillis() - startTime
            );

        } catch (Exception e) {
            // Restaura tamanho em caso de erro
            try {
                driver.manage().window().setSize(new Dimension(1920, 1080));
            } catch (Exception ignored) {}

            return createErrorResult("Responsividade Tablet", "Design", formUrl, e);
        }
    }

    /**
     * Valida tamanho de fonte
     */
    public TestResult validateFontSize(WebDriver driver, String formUrl) {
        long startTime = System.currentTimeMillis();

        try {
            driver.get(formUrl);
            Thread.sleep(1000);

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
                    if (size >= 14) {
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
                return createTestResult(
                        "Tamanho de Fonte",
                        "Design",
                        true,
                        100.0,
                        "Nenhum elemento de texto encontrado",
                        formUrl,
                        System.currentTimeMillis() - startTime
                );
            }

            double score = (elementosComFonteAdequada * 100.0) / totalElementos;
            boolean passou = score >= 90;

            return createTestResult(
                    "Tamanho de Fonte",
                    "Design",
                    passou,
                    score,
                    String.format("%d/%d elementos com fonte >= 14px",
                            elementosComFonteAdequada, totalElementos),
                    formUrl,
                    System.currentTimeMillis() - startTime
            );

        } catch (Exception e) {
            return createErrorResult("Tamanho de Fonte", "Design", formUrl, e);
        }
    }

    /**
     * Valida espaçamento entre campos
     */
    public TestResult validateFieldSpacing(WebDriver driver, String formUrl) {
        long startTime = System.currentTimeMillis();

        try {
            driver.get(formUrl);
            Thread.sleep(1000);

            List<WebElement> campos = driver.findElements(
                    By.cssSelector("input, select, textarea")
            );

            if (campos.size() < 2) {
                return createTestResult(
                        "Espaçamento Entre Campos",
                        "Design",
                        true,
                        100.0,
                        "Menos de 2 campos - espaçamento não aplicável",
                        formUrl,
                        System.currentTimeMillis() - startTime
                );
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

            return createTestResult(
                    "Espaçamento Entre Campos",
                    "Design",
                    passou,
                    score,
                    String.format("%d/%d campos com espaçamento adequado",
                            camposComEspacamento, campos.size()),
                    formUrl,
                    System.currentTimeMillis() - startTime
            );

        } catch (Exception e) {
            return createErrorResult("Espaçamento Entre Campos", "Design", formUrl, e);
        }
    }

    // ==================== MÉTODOS AUXILIARES ====================

    private TestResult createTestResult(String testName, String category,
                                        boolean passed, double score,
                                        String details, String formUrl, long execTime) {
        TestResult result = new TestResult();
        result.setTestName(testName);
        result.setCategory(category);
        result.setPassed(passed);
        result.setScore(score);
        result.setDetails(details);
        result.setFormUrl(formUrl);
        result.setExecutionTime(execTime);
        result.setTimestamp(new Timestamp(System.currentTimeMillis()));
        return result;
    }

    private TestResult createErrorResult(String testName, String category,
                                         String formUrl, Exception e) {
        TestResult result = new TestResult();
        result.setTestName(testName);
        result.setCategory(category);
        result.setPassed(false);
        result.setScore(0.0);
        result.setDetails("Erro durante execução: " + e.getMessage());
        result.setFormUrl(formUrl);
        result.setExecutionTime(0L);
        result.setTimestamp(new Timestamp(System.currentTimeMillis()));
        return result;
    }
}