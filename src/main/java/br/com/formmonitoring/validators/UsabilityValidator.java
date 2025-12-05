package br.com.formmonitoring.validators;

import br.com.formmonitoring.model.TestResult;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.sql.Timestamp;
import java.util.List;

/**
 * Validador de Usabilidade de Formulários
 * Contém toda a lógica de validação reutilizável
 */
public class UsabilityValidator {

    /**
     * Valida se o formulário tem um número ideal de campos
     */
    public TestResult validateFieldCount(WebDriver driver, String formUrl) {
        long startTime = System.currentTimeMillis();

        try {
            driver.get(formUrl);
            Thread.sleep(1000); // Aguarda carregamento

            List<WebElement> campos = driver.findElements(
                    By.cssSelector("input:not([type='submit']):not([type='button']):not([type='hidden']), select, textarea")
            );

            int totalCampos = campos.size();
            boolean passou = totalCampos >= 3 && totalCampos <= 10;

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

            return createTestResult(
                    "Número Ideal de Campos",
                    "Usabilidade",
                    passou,
                    score,
                    String.format("Formulário possui %d campos (ideal: 5-7)", totalCampos),
                    formUrl,
                    System.currentTimeMillis() - startTime
            );

        } catch (Exception e) {
            return createErrorResult("Número Ideal de Campos", "Usabilidade", formUrl, e);
        }
    }

    /**
     * Valida se o botão de submit está visível
     */
    public TestResult validateSubmitButton(WebDriver driver, String formUrl) {
        long startTime = System.currentTimeMillis();

        try {
            driver.get(formUrl);
            Thread.sleep(1000);

            List<WebElement> submitButtons = driver.findElements(
                    By.cssSelector("input[type='submit'], button[type='submit']")
            );

            int botoesVisiveis = 0;
            for (WebElement button : submitButtons) {
                if (button.isDisplayed()) {
                    botoesVisiveis++;
                }
            }

            boolean passou = botoesVisiveis > 0;
            double score = passou ? 100.0 : 0.0;

            return createTestResult(
                    "Botão Submit Visível",
                    "Usabilidade",
                    passou,
                    score,
                    String.format("%d botões de submit visíveis encontrados", botoesVisiveis),
                    formUrl,
                    System.currentTimeMillis() - startTime
            );

        } catch (Exception e) {
            return createErrorResult("Botão Submit Visível", "Usabilidade", formUrl, e);
        }
    }

    /**
     * Valida mensagens de erro no formulário
     */
    public TestResult validateErrorMessages(WebDriver driver, String formUrl) {
        long startTime = System.currentTimeMillis();

        try {
            driver.get(formUrl);
            Thread.sleep(1000);

            List<WebElement> errorMessages = driver.findElements(
                    By.cssSelector(".error, .error-message, .invalid-feedback, [class*='error']")
            );

            List<WebElement> campos = driver.findElements(
                    By.cssSelector("input:not([type='submit']):not([type='button']), select, textarea")
            );

            boolean temEstrutura = !errorMessages.isEmpty() || campos.isEmpty();
            double score = temEstrutura ? 100.0 : 50.0;

            return createTestResult(
                    "Estrutura de Mensagens de Erro",
                    "Usabilidade",
                    temEstrutura,
                    score,
                    String.format("Encontrados %d containers para mensagens de erro", errorMessages.size()),
                    formUrl,
                    System.currentTimeMillis() - startTime
            );

        } catch (Exception e) {
            return createErrorResult("Estrutura de Mensagens de Erro", "Usabilidade", formUrl, e);
        }
    }

    /**
     * Valida máscaras de entrada nos campos
     */
    public TestResult validateInputMasks(WebDriver driver, String formUrl) {
        long startTime = System.currentTimeMillis();

        try {
            driver.get(formUrl);
            Thread.sleep(1000);

            List<WebElement> camposComMascara = driver.findElements(
                    By.cssSelector("input[placeholder], input[pattern], input[maxlength]")
            );

            List<WebElement> todosCampos = driver.findElements(
                    By.cssSelector("input[type='text'], input[type='tel'], input[type='number']")
            );

            if (todosCampos.isEmpty()) {
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

            double score = (camposComMascara.size() * 100.0) / todosCampos.size();
            boolean passou = score >= 60;

            return createTestResult(
                    "Máscaras de Entrada",
                    "Usabilidade",
                    passou,
                    score,
                    String.format("%d/%d campos com máscaras ou validação",
                            camposComMascara.size(), todosCampos.size()),
                    formUrl,
                    System.currentTimeMillis() - startTime
            );

        } catch (Exception e) {
            return createErrorResult("Máscaras de Entrada", "Usabilidade", formUrl, e);
        }
    }

    /**
     * Valida validação de email
     */
    public TestResult validateEmailValidation(WebDriver driver, String formUrl) {
        long startTime = System.currentTimeMillis();

        try {
            driver.get(formUrl);
            Thread.sleep(1000);

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

            return createTestResult(
                    "Validação de Email",
                    "Validação",
                    passou,
                    score,
                    String.format("%d/%d campos email com validação pattern",
                            emailsComPattern, emailInputs.size()),
                    formUrl,
                    System.currentTimeMillis() - startTime
            );

        } catch (Exception e) {
            return createErrorResult("Validação de Email", "Validação", formUrl, e);
        }
    }

    /**
     * Valida agrupamento lógico de campos
     */
    public TestResult validateFieldGrouping(WebDriver driver, String formUrl) {
        long startTime = System.currentTimeMillis();

        try {
            driver.get(formUrl);
            Thread.sleep(1000);

            List<WebElement> fieldsets = driver.findElements(By.tagName("fieldset"));
            List<WebElement> sections = driver.findElements(
                    By.cssSelector("div[class*='group'], div[class*='section'], .form-group")
            );

            int gruposEncontrados = fieldsets.size() + sections.size();
            boolean passou = gruposEncontrados > 0;
            double score = passou ? Math.min(100.0, gruposEncontrados * 25) : 50.0;

            return createTestResult(
                    "Agrupamento Lógico de Campos",
                    "Design",
                    passou,
                    score,
                    String.format("Encontrados %d agrupamentos de campos", gruposEncontrados),
                    formUrl,
                    System.currentTimeMillis() - startTime
            );

        } catch (Exception e) {
            return createErrorResult("Agrupamento Lógico de Campos", "Design", formUrl, e);
        }
    }

    // ==================== MÉTODOS AUXILIARES ====================

    /**
     * Cria um TestResult padrão
     */
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

    /**
     * Cria um TestResult para erros
     */
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