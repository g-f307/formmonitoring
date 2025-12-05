package br.com.formmonitoring.validators;

import br.com.formmonitoring.model.TestResult;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.sql.Timestamp;
import java.util.List;

public class UsabilityValidator {

    public TestResult validateFieldCount(WebDriver driver, String formUrl) {
        long startTime = System.currentTimeMillis();

        try {
            driver.get(formUrl);
            Thread.sleep(1000);

            List<WebElement> campos = driver.findElements(
                    By.cssSelector("input:not([type='submit']):not([type='button']):not([type='hidden']), select, textarea")
            );

            int totalCampos = campos.size();

            boolean passou = totalCampos >= 3 && totalCampos <= 10;

            double score;
            if (totalCampos >= 4 && totalCampos <= 8) {
                score = 100.0;
            } else if (totalCampos >= 3 && totalCampos <= 10) {
                score = 85.0;
            } else if (totalCampos > 10) {
                score = Math.max(50.0, 100.0 - (totalCampos - 10) * 5);
            } else {
                score = 60.0;
            }

            return createTestResult(
                    "Número Ideal de Campos",
                    "Usabilidade",
                    passou,
                    score,
                    String.format("Formulário possui %d campos (ideal: 3-10)", totalCampos),
                    formUrl,
                    System.currentTimeMillis() - startTime
            );

        } catch (Exception e) {
            return createErrorResult("Número Ideal de Campos", "Usabilidade", formUrl, e);
        }
    }

    public TestResult validateSubmitButton(WebDriver driver, String formUrl) {
        long startTime = System.currentTimeMillis();

        try {
            driver.get(formUrl);
            Thread.sleep(1000);

            List<WebElement> submitButtons = driver.findElements(
                    By.cssSelector("input[type='submit'], button[type='submit']")
            );

            int botoesVisiveis = 0;
            String textoMaisLongo = "";

            for (WebElement button : submitButtons) {
                if (button.isDisplayed()) {
                    botoesVisiveis++;

                    String texto = button.getAttribute("value");
                    if (texto == null || texto.isEmpty()) {
                        texto = button.getText();
                    }

                    if (texto != null && texto.length() > textoMaisLongo.length()) {
                        textoMaisLongo = texto;
                    }
                }
            }

            boolean passou = botoesVisiveis > 0;

            double score = passou ? 100.0 : 0.0;

            if (passou && textoMaisLongo.length() > 5) {
                score = 100.0;
            } else if (passou && textoMaisLongo.length() > 2) {
                score = 80.0;
            }

            return createTestResult(
                    "Botão Submit Visível",
                    "Usabilidade",
                    passou,
                    score,
                    String.format("%d botões visíveis (texto: '%s')", botoesVisiveis, textoMaisLongo),
                    formUrl,
                    System.currentTimeMillis() - startTime
            );

        } catch (Exception e) {
            return createErrorResult("Botão Submit Visível", "Usabilidade", formUrl, e);
        }
    }

    public TestResult validateErrorMessages(WebDriver driver, String formUrl) {
        long startTime = System.currentTimeMillis();

        try {
            driver.get(formUrl);
            Thread.sleep(1000);

            List<WebElement> errorContainers = driver.findElements(
                    By.cssSelector(".error, .error-message, .invalid-feedback, [class*='error'], [id*='error'], [id*='Error']")
            );

            List<WebElement> campos = driver.findElements(
                    By.cssSelector("input:not([type='submit']):not([type='button']), select, textarea")
            );

            boolean temEstrutura = errorContainers.size() > 0;

            double score;
            if (errorContainers.size() >= campos.size()) {
                score = 100.0;
            } else if (errorContainers.size() >= campos.size() / 2) {
                score = 90.0;
            } else if (errorContainers.size() > 0) {
                score = 70.0;
            } else {
                score = 0.0;
            }

            return createTestResult(
                    "Estrutura de Mensagens de Erro",
                    "Usabilidade",
                    temEstrutura,
                    score,
                    String.format("Encontrados %d containers de erro para %d campos",
                            errorContainers.size(), campos.size()),
                    formUrl,
                    System.currentTimeMillis() - startTime
            );

        } catch (Exception e) {
            return createErrorResult("Estrutura de Mensagens de Erro", "Usabilidade", formUrl, e);
        }
    }

    public TestResult validateInputMasks(WebDriver driver, String formUrl) {
        long startTime = System.currentTimeMillis();

        try {
            driver.get(formUrl);
            Thread.sleep(1000);

            List<WebElement> camposComMascara = driver.findElements(
                    By.cssSelector("input[placeholder], input[pattern], input[maxlength], input[minlength]")
            );

            List<WebElement> todosCamposTexto = driver.findElements(
                    By.cssSelector("input[type='text'], input[type='tel'], input[type='email'], input[type='number']")
            );

            if (todosCamposTexto.isEmpty()) {
                return createTestResult(
                        "Máscaras de Entrada",
                        "Usabilidade",
                        true,
                        100.0,
                        "Nenhum campo que necessite máscara",
                        formUrl,
                        System.currentTimeMillis() - startTime
                );
            }

            double percentual = (camposComMascara.size() * 100.0) / todosCamposTexto.size();
            boolean passou = percentual >= 50;

            return createTestResult(
                    "Máscaras de Entrada",
                    "Usabilidade",
                    passou,
                    percentual,
                    String.format("%d/%d campos com máscaras ou validação (%.0f%%)",
                            camposComMascara.size(), todosCamposTexto.size(), percentual),
                    formUrl,
                    System.currentTimeMillis() - startTime
            );

        } catch (Exception e) {
            return createErrorResult("Máscaras de Entrada", "Usabilidade", formUrl, e);
        }
    }

    public TestResult validateEmailValidation(WebDriver driver, String formUrl) {
        long startTime = System.currentTimeMillis();

        try {
            driver.get(formUrl);
            Thread.sleep(1000);

            List<WebElement> emailInputs = driver.findElements(
                    By.cssSelector("input[type='email']")
            );

            List<WebElement> camposTexto = driver.findElements(
                    By.cssSelector("input[type='text']")
            );

            int emailsCorretos = emailInputs.size();
            int emailsIncorretos = 0;

            for (WebElement campo : camposTexto) {
                String name = campo.getAttribute("name");
                String id = campo.getAttribute("id");
                String placeholder = campo.getAttribute("placeholder");

                // Detecta se é campo de email com type errado
                String nameStr = (name != null ? name.toLowerCase() : "");
                String idStr = (id != null ? id.toLowerCase() : "");
                String placeholderStr = (placeholder != null ? placeholder.toLowerCase() : "");

                if (nameStr.contains("email") || nameStr.contains("e-mail") ||
                        idStr.contains("email") || idStr.contains("e-mail") ||
                        placeholderStr.contains("email") || placeholderStr.contains("@")) {
                    emailsIncorretos++;
                }
            }

            boolean passou = emailsIncorretos == 0;

            double score;
            if (emailsIncorretos == 0 && emailsCorretos > 0) {
                score = 100.0;
            } else if (emailsIncorretos == 0) {
                score = 100.0;
            } else {
                score = 20.0;
            }

            String details;
            if (emailsIncorretos > 0) {
                details = String.format("❌ %d campos email com type='text' incorreto (deveria ser type='email')", emailsIncorretos);
            } else if (emailsCorretos > 0) {
                details = String.format("✅ %d campos com type='email' correto", emailsCorretos);
            } else {
                details = "Nenhum campo de email encontrado";
            }

            return createTestResult(
                    "Validação de Email",
                    "Validação",
                    passou,
                    score,
                    details,
                    formUrl,
                    System.currentTimeMillis() - startTime
            );

        } catch (Exception e) {
            return createErrorResult("Validação de Email", "Validação", formUrl, e);
        }
    }

    public TestResult validateFieldGrouping(WebDriver driver, String formUrl) {
        long startTime = System.currentTimeMillis();

        try {
            driver.get(formUrl);
            Thread.sleep(1000);

            List<WebElement> fieldsets = driver.findElements(By.tagName("fieldset"));
            List<WebElement> sections = driver.findElements(
                    By.cssSelector("div[class*='group'], div[class*='form-group'], .form-group, [class*='field-group']")
            );

            int gruposEncontrados = fieldsets.size() + sections.size();

            List<WebElement> campos = driver.findElements(
                    By.cssSelector("input:not([type='submit']):not([type='button']), select, textarea")
            );

            boolean passou = campos.size() <= 3 || gruposEncontrados > 0;

            double score;
            if (gruposEncontrados >= campos.size()) {
                score = 100.0;
            } else if (gruposEncontrados >= Math.max(1, campos.size() / 3)) {
                score = 90.0;
            } else if (gruposEncontrados > 0) {
                score = 70.0;
            } else if (campos.size() <= 3) {
                score = 100.0;
            } else {
                score = 30.0;
            }

            return createTestResult(
                    "Agrupamento Lógico de Campos",
                    "Design",
                    passou,
                    score,
                    String.format("Encontrados %d agrupamentos para %d campos",
                            gruposEncontrados, campos.size()),
                    formUrl,
                    System.currentTimeMillis() - startTime
            );

        } catch (Exception e) {
            return createErrorResult("Agrupamento Lógico de Campos", "Design", formUrl, e);
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