package br.com.formmonitoring.validators;

import br.com.formmonitoring.model.TestResult;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.sql.Timestamp;
import java.util.List;

public class PerformanceValidator {

    public TestResult validateLoadingTime(WebDriver driver, String formUrl) {
        long inicio = System.currentTimeMillis();

        try {
            driver.get(formUrl);
            driver.findElement(By.tagName("body"));

            long tempoCarregamento = System.currentTimeMillis() - inicio;
            boolean passou = tempoCarregamento <= 5000;

            double score = 100.0;
            if (tempoCarregamento > 5000) {
                score = Math.max(50.0, 100.0 - ((tempoCarregamento - 5000) / 100.0));
            }

            return createTestResult(
                    "Tempo de Carregamento",
                    "Performance",
                    passou,
                    score,
                    String.format("Página carregou em %d ms (limite: 5000ms)", tempoCarregamento),
                    formUrl,
                    tempoCarregamento
            );

        } catch (Exception e) {
            return createErrorResult("Tempo de Carregamento", "Performance", formUrl, e);
        }
    }

    public TestResult validateMobileResponsiveness(WebDriver driver, String formUrl) {
        long startTime = System.currentTimeMillis();

        try {
            driver.manage().window().setSize(new Dimension(375, 667));
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

            boolean passou = campos.isEmpty() || camposVisiveis == campos.size();
            double score = campos.isEmpty() ? 100.0 : (camposVisiveis * 100.0) / campos.size();

            driver.manage().window().setSize(new Dimension(1920, 1080));

            return createTestResult(
                    "Responsividade Mobile",
                    "Design",
                    passou,
                    score,
                    String.format("%d/%d campos visíveis em mobile", camposVisiveis, campos.size()),
                    formUrl,
                    System.currentTimeMillis() - startTime
            );

        } catch (Exception e) {
            try {
                driver.manage().window().setSize(new Dimension(1920, 1080));
            } catch (Exception ignored) {}
            return createErrorResult("Responsividade Mobile", "Design", formUrl, e);
        }
    }

    public TestResult validateTabletResponsiveness(WebDriver driver, String formUrl) {
        long startTime = System.currentTimeMillis();

        try {
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

            boolean passou = campos.isEmpty() || camposVisiveis == campos.size();
            double score = campos.isEmpty() ? 100.0 : (camposVisiveis * 100.0) / campos.size();

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
            try {
                driver.manage().window().setSize(new Dimension(1920, 1080));
            } catch (Exception ignored) {}
            return createErrorResult("Responsividade Tablet", "Design", formUrl, e);
        }
    }

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

            for (WebElement label : labels) {
                try {
                    String fontSize = label.getCssValue("font-size");
                    if (fontSize != null && !fontSize.isEmpty()) {
                        double size = Double.parseDouble(fontSize.replace("px", ""));
                        if (size >= 12) { // Mínimo 12px
                            elementosComFonteAdequada++;
                        }
                    }
                } catch (Exception ignored) {}
            }

            for (WebElement input : inputs) {
                try {
                    String fontSize = input.getCssValue("font-size");
                    if (fontSize != null && !fontSize.isEmpty()) {
                        double size = Double.parseDouble(fontSize.replace("px", ""));
                        if (size >= 12) {
                            elementosComFonteAdequada++;
                        }
                    }
                } catch (Exception ignored) {}
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
            boolean passou = score >= 80;

            return createTestResult(
                    "Tamanho de Fonte",
                    "Design",
                    passou,
                    score,
                    String.format("%d/%d elementos com fonte >= 12px (%.0f%%)",
                            elementosComFonteAdequada, totalElementos, score),
                    formUrl,
                    System.currentTimeMillis() - startTime
            );

        } catch (Exception e) {
            return createErrorResult("Tamanho de Fonte", "Design", formUrl, e);
        }
    }

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
                try {
                    String marginBottom = campo.getCssValue("margin-bottom");
                    String paddingBottom = campo.getCssValue("padding-bottom");

                    WebElement parent = campo.findElement(By.xpath(".."));
                    String parentMargin = parent.getCssValue("margin-bottom");
                    String parentPadding = parent.getCssValue("padding-bottom");

                    if ((marginBottom != null && !marginBottom.equals("0px")) ||
                            (paddingBottom != null && !paddingBottom.equals("0px")) ||
                            (parentMargin != null && !parentMargin.equals("0px")) ||
                            (parentPadding != null && !parentPadding.equals("0px"))) {
                        camposComEspacamento++;
                    }
                } catch (Exception ignored) {}
            }

            double score = (camposComEspacamento * 100.0) / campos.size();
            boolean passou = score >= 60;

            return createTestResult(
                    "Espaçamento Entre Campos",
                    "Design",
                    passou,
                    score,
                    String.format("%d/%d campos com espaçamento adequado (%.0f%%)",
                            camposComEspacamento, campos.size(), score),
                    formUrl,
                    System.currentTimeMillis() - startTime
            );

        } catch (Exception e) {
            return createErrorResult("Espaçamento Entre Campos", "Design", formUrl, e);
        }
    }

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