package br.com.formmonitoring.runner;

import br.com.formmonitoring.config.SeleniumConfig;
import br.com.formmonitoring.dao.TestResultDAO;
import br.com.formmonitoring.model.TestResult;
import br.com.formmonitoring.validators.AccessibilityValidator;
import br.com.formmonitoring.validators.PerformanceValidator;
import br.com.formmonitoring.validators.UsabilityValidator;
import org.openqa.selenium.WebDriver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Runner refatorado para executar validações programaticamente
 * Usa os Validators para lógica reutilizável
 */
public class TestRunner {

    private static final String FORM_URL = "http://localhost:8080/form-monitoring/jsp/form-exemplo.jsp";

    /**
     * Executa todos os testes
     */
    public static TestExecutionResult runAllTests() {
        long startTime = System.currentTimeMillis();
        TestExecutionResult executionResult = new TestExecutionResult();

        // Executar testes de acessibilidade
        TestExecutionResult accessibilityResult = runAccessibilityTests();
        executionResult.merge(accessibilityResult);

        // Executar testes de usabilidade
        TestExecutionResult usabilityResult = runUsabilityTests();
        executionResult.merge(usabilityResult);

        // Executar testes de performance
        TestExecutionResult performanceResult = runPerformanceTests();
        executionResult.merge(performanceResult);

        long totalTime = System.currentTimeMillis() - startTime;
        executionResult.setExecutionTime(totalTime);

        return executionResult;
    }

    /**
     * Executa testes por categoria
     */
    public static TestExecutionResult runTestsByCategory(String category) {
        switch (category.toLowerCase()) {
            case "acessibilidade":
                return runAccessibilityTests();
            case "usabilidade":
                return runUsabilityTests();
            case "performance":
                return runPerformanceTests();
            default:
                throw new IllegalArgumentException("Categoria inválida: " + category);
        }
    }

    /**
     * Executa testes de acessibilidade
     */
    public static TestExecutionResult runAccessibilityTests() {
        long startTime = System.currentTimeMillis();
        TestExecutionResult result = new TestExecutionResult();
        TestResultDAO dao = new TestResultDAO();
        WebDriver driver = null;

        try {
            System.out.println("=== Iniciando testes de Acessibilidade ===");
            driver = SeleniumConfig.getDriver();
            AccessibilityValidator validator = new AccessibilityValidator();

            // Executar validações
            TestResult r1 = validator.validateAssociatedLabels(driver, FORM_URL);
            dao.save(r1);
            result.addTestResult(r1);
            System.out.println("✓ " + r1.getTestName() + ": " + (r1.isPassed() ? "PASSOU" : "FALHOU"));

            TestResult r2 = validator.validateRequiredFieldsIndication(driver, FORM_URL);
            dao.save(r2);
            result.addTestResult(r2);
            System.out.println("✓ " + r2.getTestName() + ": " + (r2.isPassed() ? "PASSOU" : "FALHOU"));

            TestResult r3 = validator.validateARIAAttributes(driver, FORM_URL);
            dao.save(r3);
            result.addTestResult(r3);
            System.out.println("✓ " + r3.getTestName() + ": " + (r3.isPassed() ? "PASSOU" : "FALHOU"));

            TestResult r4 = validator.validateElementVisibility(driver, FORM_URL);
            dao.save(r4);
            result.addTestResult(r4);
            System.out.println("✓ " + r4.getTestName() + ": " + (r4.isPassed() ? "PASSOU" : "FALHOU"));

        } catch (Exception e) {
            System.err.println("Erro ao executar testes de acessibilidade: " + e.getMessage());
            e.printStackTrace();
        } finally {
            SeleniumConfig.quitDriver(driver);
            System.out.println("=== Testes de Acessibilidade finalizados ===\n");
        }

        result.setExecutionTime(System.currentTimeMillis() - startTime);
        return result;
    }

    /**
     * Executa testes de usabilidade
     */
    public static TestExecutionResult runUsabilityTests() {
        long startTime = System.currentTimeMillis();
        TestExecutionResult result = new TestExecutionResult();
        TestResultDAO dao = new TestResultDAO();
        WebDriver driver = null;

        try {
            System.out.println("=== Iniciando testes de Usabilidade ===");
            driver = SeleniumConfig.getDriver();
            UsabilityValidator validator = new UsabilityValidator();

            // Executar validações
            TestResult r1 = validator.validateFieldCount(driver, FORM_URL);
            dao.save(r1);
            result.addTestResult(r1);
            System.out.println("✓ " + r1.getTestName() + ": " + (r1.isPassed() ? "PASSOU" : "FALHOU"));

            TestResult r2 = validator.validateSubmitButton(driver, FORM_URL);
            dao.save(r2);
            result.addTestResult(r2);
            System.out.println("✓ " + r2.getTestName() + ": " + (r2.isPassed() ? "PASSOU" : "FALHOU"));

            TestResult r3 = validator.validateErrorMessages(driver, FORM_URL);
            dao.save(r3);
            result.addTestResult(r3);
            System.out.println("✓ " + r3.getTestName() + ": " + (r3.isPassed() ? "PASSOU" : "FALHOU"));

            TestResult r4 = validator.validateInputMasks(driver, FORM_URL);
            dao.save(r4);
            result.addTestResult(r4);
            System.out.println("✓ " + r4.getTestName() + ": " + (r4.isPassed() ? "PASSOU" : "FALHOU"));

            TestResult r5 = validator.validateEmailValidation(driver, FORM_URL);
            dao.save(r5);
            result.addTestResult(r5);
            System.out.println("✓ " + r5.getTestName() + ": " + (r5.isPassed() ? "PASSOU" : "FALHOU"));

            TestResult r6 = validator.validateFieldGrouping(driver, FORM_URL);
            dao.save(r6);
            result.addTestResult(r6);
            System.out.println("✓ " + r6.getTestName() + ": " + (r6.isPassed() ? "PASSOU" : "FALHOU"));

        } catch (Exception e) {
            System.err.println("Erro ao executar testes de usabilidade: " + e.getMessage());
            e.printStackTrace();
        } finally {
            SeleniumConfig.quitDriver(driver);
            System.out.println("=== Testes de Usabilidade finalizados ===\n");
        }

        result.setExecutionTime(System.currentTimeMillis() - startTime);
        return result;
    }

    /**
     * Executa testes de performance
     */
    public static TestExecutionResult runPerformanceTests() {
        long startTime = System.currentTimeMillis();
        TestExecutionResult result = new TestExecutionResult();
        TestResultDAO dao = new TestResultDAO();
        WebDriver driver = null;

        try {
            System.out.println("=== Iniciando testes de Performance ===");
            driver = SeleniumConfig.getDriver();
            PerformanceValidator validator = new PerformanceValidator();

            // Executar validações
            TestResult r1 = validator.validateLoadingTime(driver, FORM_URL);
            dao.save(r1);
            result.addTestResult(r1);
            System.out.println("✓ " + r1.getTestName() + ": " + (r1.isPassed() ? "PASSOU" : "FALHOU"));

            TestResult r2 = validator.validateMobileResponsiveness(driver, FORM_URL);
            dao.save(r2);
            result.addTestResult(r2);
            System.out.println("✓ " + r2.getTestName() + ": " + (r2.isPassed() ? "PASSOU" : "FALHOU"));

            TestResult r3 = validator.validateTabletResponsiveness(driver, FORM_URL);
            dao.save(r3);
            result.addTestResult(r3);
            System.out.println("✓ " + r3.getTestName() + ": " + (r3.isPassed() ? "PASSOU" : "FALHOU"));

            TestResult r4 = validator.validateFontSize(driver, FORM_URL);
            dao.save(r4);
            result.addTestResult(r4);
            System.out.println("✓ " + r4.getTestName() + ": " + (r4.isPassed() ? "PASSOU" : "FALHOU"));

            TestResult r5 = validator.validateFieldSpacing(driver, FORM_URL);
            dao.save(r5);
            result.addTestResult(r5);
            System.out.println("✓ " + r5.getTestName() + ": " + (r5.isPassed() ? "PASSOU" : "FALHOU"));

        } catch (Exception e) {
            System.err.println("Erro ao executar testes de performance: " + e.getMessage());
            e.printStackTrace();
        } finally {
            SeleniumConfig.quitDriver(driver);
            System.out.println("=== Testes de Performance finalizados ===\n");
        }

        result.setExecutionTime(System.currentTimeMillis() - startTime);
        return result;
    }

    /**
     * Classe para encapsular resultados da execução
     */
    public static class TestExecutionResult {
        private List<TestResult> results = new ArrayList<>();
        private Map<String, CategoryResult> categoryResults = new HashMap<>();
        private long executionTime;
        private List<String> failureMessages = new ArrayList<>();

        public void addTestResult(TestResult result) {
            results.add(result);

            // Atualiza estatísticas por categoria
            String category = result.getCategory();
            CategoryResult cr = categoryResults.computeIfAbsent(category, k -> new CategoryResult(category));
            cr.totalTests++;
            if (result.isPassed()) {
                cr.passedTests++;
            } else {
                cr.failedTests++;
                failureMessages.add(result.getTestName() + ": " + result.getDetails());
            }
            cr.runTime += result.getExecutionTime();
        }

        public void merge(TestExecutionResult other) {
            this.results.addAll(other.results);
            this.failureMessages.addAll(other.failureMessages);

            for (Map.Entry<String, CategoryResult> entry : other.categoryResults.entrySet()) {
                String key = entry.getKey();
                CategoryResult otherCr = entry.getValue();

                CategoryResult cr = this.categoryResults.computeIfAbsent(key, k -> new CategoryResult(key));
                cr.totalTests += otherCr.totalTests;
                cr.passedTests += otherCr.passedTests;
                cr.failedTests += otherCr.failedTests;
                cr.runTime += otherCr.runTime;
            }
        }

        public int getTotalTests() {
            return results.size();
        }

        public int getPassedTests() {
            return (int) results.stream().filter(TestResult::isPassed).count();
        }

        public int getFailedTests() {
            return getTotalTests() - getPassedTests();
        }

        public boolean isSuccess() {
            return getFailedTests() == 0;
        }

        public double getSuccessRate() {
            int total = getTotalTests();
            if (total == 0) return 0.0;
            return (getPassedTests() * 100.0) / total;
        }

        // Getters e Setters
        public Map<String, CategoryResult> getCategoryResults() {
            return categoryResults;
        }

        public long getExecutionTime() {
            return executionTime;
        }

        public void setExecutionTime(long executionTime) {
            this.executionTime = executionTime;
        }

        public List<String> getFailureMessages() {
            return failureMessages;
        }

        public void setFailureMessages(List<String> failureMessages) {
            this.failureMessages = failureMessages;
        }
    }

    /**
     * Classe para resultado de categoria
     */
    public static class CategoryResult {
        public String category;
        public int totalTests;
        public int passedTests;
        public int failedTests;
        public boolean success;
        public long runTime;

        public CategoryResult(String category) {
            this.category = category;
        }
    }

    /**
     * Main para executar via linha de comando
     */
    public static void main(String[] args) {
        System.out.println("=== Executando Todos os Testes ===\n");

        TestExecutionResult result = runAllTests();

        System.out.println("\n=== RESULTADOS FINAIS ===");
        System.out.println("Total de testes: " + result.getTotalTests());
        System.out.println("Testes passados: " + result.getPassedTests());
        System.out.println("Testes falhados: " + result.getFailedTests());
        System.out.printf("Taxa de sucesso: %.2f%%\n", result.getSuccessRate());
        System.out.println("Tempo de execução: " + result.getExecutionTime() + "ms");

        if (!result.isSuccess()) {
            System.out.println("\n=== FALHAS ===");
            for (String failure : result.getFailureMessages()) {
                System.out.println("  • " + failure);
            }
        }

        System.out.println("\n=== RESULTADOS POR CATEGORIA ===");
        for (Map.Entry<String, CategoryResult> entry : result.getCategoryResults().entrySet()) {
            CategoryResult cr = entry.getValue();
            System.out.printf("%s: %d/%d passados (%.1fms)\n",
                    cr.category, cr.passedTests, cr.totalTests, (double)cr.runTime);
        }
    }
}