package br.com.formmonitoring.runner;

import br.com.formmonitoring.tests.FormAccessibilityTest;
import br.com.formmonitoring.tests.FormPerformanceTest;
import br.com.formmonitoring.tests.FormUsabilityTest;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Runner para executar todos os testes programaticamente
 */
public class TestRunner {

    /**
     * Executa todos os testes e retorna resultado consolidado
     */
    public static TestExecutionResult runAllTests() {
        long startTime = System.currentTimeMillis();

        TestExecutionResult executionResult = new TestExecutionResult();
        List<String> failureMessages = new ArrayList<>();

        // Executar testes de acessibilidade
        Result accessibilityResult = JUnitCore.runClasses(FormAccessibilityTest.class);
        executionResult.addResult("Acessibilidade", accessibilityResult);
        collectFailures(accessibilityResult, failureMessages);

        // Executar testes de usabilidade
        Result usabilityResult = JUnitCore.runClasses(FormUsabilityTest.class);
        executionResult.addResult("Usabilidade", usabilityResult);
        collectFailures(usabilityResult, failureMessages);

        // Executar testes de performance
        Result performanceResult = JUnitCore.runClasses(FormPerformanceTest.class);
        executionResult.addResult("Performance", performanceResult);
        collectFailures(performanceResult, failureMessages);

        long totalTime = System.currentTimeMillis() - startTime;
        executionResult.setExecutionTime(totalTime);
        executionResult.setFailureMessages(failureMessages);

        return executionResult;
    }

    /**
     * Executa testes de uma categoria específica
     */
    public static TestExecutionResult runTestsByCategory(String category) {
        long startTime = System.currentTimeMillis();
        TestExecutionResult executionResult = new TestExecutionResult();
        List<String> failureMessages = new ArrayList<>();

        Result result = null;

        switch (category.toLowerCase()) {
            case "acessibilidade":
                result = JUnitCore.runClasses(FormAccessibilityTest.class);
                break;
            case "usabilidade":
                result = JUnitCore.runClasses(FormUsabilityTest.class);
                break;
            case "performance":
                result = JUnitCore.runClasses(FormPerformanceTest.class);
                break;
            default:
                throw new IllegalArgumentException("Categoria inválida: " + category);
        }

        if (result != null) {
            executionResult.addResult(category, result);
            collectFailures(result, failureMessages);
        }

        long totalTime = System.currentTimeMillis() - startTime;
        executionResult.setExecutionTime(totalTime);
        executionResult.setFailureMessages(failureMessages);

        return executionResult;
    }

    /**
     * Coleta mensagens de falha
     */
    private static void collectFailures(Result result, List<String> messages) {
        for (Failure failure : result.getFailures()) {
            messages.add(failure.toString());
        }
    }

    /**
     * Classe para encapsular resultados da execução
     */
    public static class TestExecutionResult {
        private Map<String, CategoryResult> categoryResults = new HashMap<>();
        private long executionTime;
        private List<String> failureMessages = new ArrayList<>();

        public void addResult(String category, Result result) {
            CategoryResult cr = new CategoryResult();
            cr.category = category;
            cr.totalTests = result.getRunCount();
            cr.failedTests = result.getFailureCount();
            cr.passedTests = result.getRunCount() - result.getFailureCount();
            cr.success = result.wasSuccessful();
            cr.runTime = result.getRunTime();

            categoryResults.put(category, cr);
        }

        public int getTotalTests() {
            return categoryResults.values().stream()
                    .mapToInt(cr -> cr.totalTests)
                    .sum();
        }

        public int getPassedTests() {
            return categoryResults.values().stream()
                    .mapToInt(cr -> cr.passedTests)
                    .sum();
        }

        public int getFailedTests() {
            return categoryResults.values().stream()
                    .mapToInt(cr -> cr.failedTests)
                    .sum();
        }

        public boolean isSuccess() {
            return categoryResults.values().stream()
                    .allMatch(cr -> cr.success);
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
    }

    /**
     * Main para executar via linha de comando
     */
    public static void main(String[] args) {
        System.out.println("=== Executando Testes de Usabilidade ===\n");

        TestExecutionResult result = runAllTests();

        System.out.println("\n=== Resultados ===");
        System.out.println("Total de testes: " + result.getTotalTests());
        System.out.println("Testes passados: " + result.getPassedTests());
        System.out.println("Testes falhados: " + result.getFailedTests());
        System.out.printf("Taxa de sucesso: %.2f%%\n", result.getSuccessRate());
        System.out.println("Tempo de execução: " + result.getExecutionTime() + "ms");

        if (!result.isSuccess()) {
            System.out.println("\n=== Falhas ===");
            for (String failure : result.getFailureMessages()) {
                System.out.println(failure);
            }
        }

        System.out.println("\n=== Resultados por Categoria ===");
        for (Map.Entry<String, CategoryResult> entry : result.getCategoryResults().entrySet()) {
            CategoryResult cr = entry.getValue();
            System.out.printf("%s: %d/%d passados (%.1fms)\n",
                    cr.category, cr.passedTests, cr.totalTests, (double)cr.runTime);
        }
    }
}