package br.com.formmonitoring.validators;

import br.com.formmonitoring.model.TestResult;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.sql.Timestamp;
import java.util.List;

public class AccessibilityValidator {

    public TestResult validateAssociatedLabels(WebDriver driver, String formUrl) {
        long startTime = System.currentTimeMillis();

        try {
            driver.get(formUrl);
            Thread.sleep(1000);

            List<WebElement> inputs = driver.findElements(
                    By.cssSelector("input:not([type='submit']):not([type='button'])")
            );

            int totalInputs = inputs.size();
            if (totalInputs == 0) {
                return createTestResult("Labels Associados", "Acessibilidade", true, 100.0,
                        "Nenhum input encontrado", formUrl, System.currentTimeMillis() - startTime);
            }

            int inputsComLabel = 0;
            for (WebElement input : inputs) {
                String id = input.getAttribute("id");

                if (id != null && !id.isEmpty()) {
                    List<WebElement> labels = driver.findElements(
                            By.cssSelector("label[for='" + id + "']")
                    );
                    if (!labels.isEmpty()) {
                        inputsComLabel++;
                        continue;
                    }
                }

                try {
                    WebElement parent = input.findElement(By.xpath("./ancestor::label"));
                    if (parent != null) {
                        inputsComLabel++;
                    }
                } catch (Exception ignored) {}
            }

            double percentual = (inputsComLabel * 100.0) / totalInputs;
            boolean passou = percentual >= 70; // 70% é aceitável

            return createTestResult(
                    "Labels Associados",
                    "Acessibilidade",
                    passou,
                    percentual,
                    String.format("%d/%d inputs possuem labels (%.0f%%)", inputsComLabel, totalInputs, percentual),
                    formUrl,
                    System.currentTimeMillis() - startTime
            );

        } catch (Exception e) {
            return createErrorResult("Labels Associados", "Acessibilidade", formUrl, e);
        }
    }

    public TestResult validateRequiredFieldsIndication(WebDriver driver, String formUrl) {
        long startTime = System.currentTimeMillis();

        try {
            driver.get(formUrl);
            Thread.sleep(1000);

            List<WebElement> required = driver.findElements(
                    By.cssSelector("input[required], select[required], textarea[required]")
            );

            if (required.isEmpty()) {
                return createTestResult(
                        "Indicação de Campos Obrigatórios",
                        "Acessibilidade",
                        true,
                        100.0,
                        "Nenhum campo obrigatório encontrado",
                        formUrl,
                        System.currentTimeMillis() - startTime
                );
            }

            int camposComIndicador = 0;
            for (WebElement campo : required) {
                try {
                    String ariaRequired = campo.getAttribute("aria-required");
                    if ("true".equals(ariaRequired)) {
                        camposComIndicador++;
                        continue;
                    }

                    WebElement parent = campo.findElement(By.xpath(".."));
                    String html = parent.getAttribute("innerHTML");
                    String className = parent.getAttribute("class");

                    if (html != null && (html.contains("*") || html.contains("obrigatório") ||
                            html.contains("required") || html.contains("Required"))) {
                        camposComIndicador++;
                    } else if (className != null && className.contains("required")) {
                        camposComIndicador++;
                    }
                } catch (Exception ignored) {}
            }

            double score = (camposComIndicador * 100.0) / required.size();
            boolean passou = score >= 60;

            return createTestResult(
                    "Indicação de Campos Obrigatórios",
                    "Acessibilidade",
                    passou,
                    score,
                    String.format("%d/%d campos obrigatórios indicados (%.0f%%)",
                            camposComIndicador, required.size(), score),
                    formUrl,
                    System.currentTimeMillis() - startTime
            );

        } catch (Exception e) {
            return createErrorResult("Indicação de Campos Obrigatórios", "Acessibilidade", formUrl, e);
        }
    }

    public TestResult validateARIAAttributes(WebDriver driver, String formUrl) {
        long startTime = System.currentTimeMillis();

        try {
            driver.get(formUrl);
            Thread.sleep(1000);

            List<WebElement> formElements = driver.findElements(
                    By.cssSelector("input, select, textarea, button")
            );

            if (formElements.isEmpty()) {
                return createTestResult("Atributos ARIA", "Acessibilidade", true, 100.0,
                        "Nenhum elemento de formulário encontrado", formUrl, System.currentTimeMillis() - startTime);
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
            boolean passou = score >= 30;

            return createTestResult(
                    "Atributos ARIA",
                    "Acessibilidade",
                    passou,
                    score,
                    String.format("%d/%d elementos possuem atributos ARIA (%.0f%%)",
                            elementosComARIA, formElements.size(), score),
                    formUrl,
                    System.currentTimeMillis() - startTime
            );

        } catch (Exception e) {
            return createErrorResult("Atributos ARIA", "Acessibilidade", formUrl, e);
        }
    }

    public TestResult validateElementVisibility(WebDriver driver, String formUrl) {
        long startTime = System.currentTimeMillis();

        try {
            driver.get(formUrl);
            Thread.sleep(1000);

            List<WebElement> inputs = driver.findElements(
                    By.cssSelector("input, select, textarea")
            );

            if (inputs.isEmpty()) {
                return createTestResult("Visibilidade de Elementos", "Acessibilidade", true, 100.0,
                        "Nenhum campo de entrada encontrado", formUrl, System.currentTimeMillis() - startTime);
            }

            int elementosVisiveis = 0;
            for (WebElement input : inputs) {
                if (input.isDisplayed()) {
                    elementosVisiveis++;
                }
            }

            double score = (elementosVisiveis * 100.0) / inputs.size();
            boolean passou = score >= 90;

            return createTestResult(
                    "Visibilidade de Elementos",
                    "Acessibilidade",
                    passou,
                    score,
                    String.format("%d/%d elementos visíveis (%.0f%%)", elementosVisiveis, inputs.size(), score),
                    formUrl,
                    System.currentTimeMillis() - startTime
            );

        } catch (Exception e) {
            return createErrorResult("Visibilidade de Elementos", "Acessibilidade", formUrl, e);
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