package br.com.formmonitoring.servlets;

import br.com.formmonitoring.config.SeleniumConfig;
import br.com.formmonitoring.dao.TestResultDAO;
import br.com.formmonitoring.model.TestResult;
import br.com.formmonitoring.validators.AccessibilityValidator;
import br.com.formmonitoring.validators.PerformanceValidator;
import br.com.formmonitoring.validators.UsabilityValidator;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.openqa.selenium.WebDriver;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Servlet para executar testes com feedback em tempo real via SSE
 */
@WebServlet("/executar-testes-visual")
public class TestExecutorServlet extends HttpServlet {

    private Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Configuração para Server-Sent Events (SSE)
        response.setContentType("text/event-stream");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Connection", "keep-alive");

        PrintWriter out = response.getWriter();

        String formType = request.getParameter("formType"); // "good" ou "bad"
        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" +
                request.getServerPort() + request.getContextPath();

        String formUrl = formType.equals("good")
                ? baseUrl + "/form-exemplo"
                : baseUrl + "/form-ruim";

        System.out.println("=== INICIANDO TESTES VISUAIS ===");
        System.out.println("Tipo: " + formType);
        System.out.println("URL: " + formUrl);

        WebDriver driver = null;
        TestResultDAO dao = new TestResultDAO();
        List<TestResult> allResults = new ArrayList<>();

        try {
            // Evento: Iniciando
            sendEvent(out, "status", Map.of(
                    "message", "Iniciando WebDriver...",
                    "progress", 0
            ));

            // IMPORTANTE: Desabilita headless para visualização
            driver = SeleniumConfig.getDriver();

            sendEvent(out, "status", Map.of(
                    "message", "WebDriver iniciado! Abrindo formulário...",
                    "progress", 10
            ));

            // Aguarda um pouco para o usuário ver
            Thread.sleep(2000);

            // ==================== TESTES DE USABILIDADE ====================
            UsabilityValidator usabilityValidator = new UsabilityValidator();
            int progress = 20;
            int increment = 60 / 6; // 6 testes de usabilidade

            // 1. Número de Campos
            sendEvent(out, "test-start", Map.of(
                    "testName", "Número Ideal de Campos",
                    "category", "Usabilidade"
            ));
            TestResult r1 = usabilityValidator.validateFieldCount(driver, formUrl);
            dao.save(r1);
            allResults.add(r1);
            progress += increment;
            sendEvent(out, "test-result", createResultMap(r1, progress));
            Thread.sleep(1500);

            // 2. Botão Submit
            sendEvent(out, "test-start", Map.of(
                    "testName", "Botão Submit Visível",
                    "category", "Usabilidade"
            ));
            TestResult r2 = usabilityValidator.validateSubmitButton(driver, formUrl);
            dao.save(r2);
            allResults.add(r2);
            progress += increment;
            sendEvent(out, "test-result", createResultMap(r2, progress));
            Thread.sleep(1500);

            // 3. Mensagens de Erro
            sendEvent(out, "test-start", Map.of(
                    "testName", "Estrutura de Mensagens de Erro",
                    "category", "Usabilidade"
            ));
            TestResult r3 = usabilityValidator.validateErrorMessages(driver, formUrl);
            dao.save(r3);
            allResults.add(r3);
            progress += increment;
            sendEvent(out, "test-result", createResultMap(r3, progress));
            Thread.sleep(1500);

            // 4. Máscaras de Entrada
            sendEvent(out, "test-start", Map.of(
                    "testName", "Máscaras de Entrada",
                    "category", "Usabilidade"
            ));
            TestResult r4 = usabilityValidator.validateInputMasks(driver, formUrl);
            dao.save(r4);
            allResults.add(r4);
            progress += increment;
            sendEvent(out, "test-result", createResultMap(r4, progress));
            Thread.sleep(1500);

            // 5. Validação Email
            sendEvent(out, "test-start", Map.of(
                    "testName", "Validação de Email",
                    "category", "Validação"
            ));
            TestResult r5 = usabilityValidator.validateEmailValidation(driver, formUrl);
            dao.save(r5);
            allResults.add(r5);
            progress += increment;
            sendEvent(out, "test-result", createResultMap(r5, progress));
            Thread.sleep(1500);

            // 6. Agrupamento
            sendEvent(out, "test-start", Map.of(
                    "testName", "Agrupamento Lógico de Campos",
                    "category", "Design"
            ));
            TestResult r6 = usabilityValidator.validateFieldGrouping(driver, formUrl);
            dao.save(r6);
            allResults.add(r6);
            progress += increment;
            sendEvent(out, "test-result", createResultMap(r6, progress));
            Thread.sleep(1500);

            // ==================== TESTES DE ACESSIBILIDADE ====================
            sendEvent(out, "status", Map.of(
                    "message", "Executando testes de acessibilidade...",
                    "progress", 85
            ));

            AccessibilityValidator accessibilityValidator = new AccessibilityValidator();

            TestResult r7 = accessibilityValidator.validateAssociatedLabels(driver, formUrl);
            dao.save(r7);
            allResults.add(r7);
            sendEvent(out, "test-result", createResultMap(r7, 90));
            Thread.sleep(1000);

            TestResult r8 = accessibilityValidator.validateRequiredFieldsIndication(driver, formUrl);
            dao.save(r8);
            allResults.add(r8);
            sendEvent(out, "test-result", createResultMap(r8, 95));
            Thread.sleep(1000);

            // ==================== RESUMO FINAL ====================
            int totalTests = allResults.size();
            int passedTests = (int) allResults.stream().filter(TestResult::isPassed).count();
            int failedTests = totalTests - passedTests;
            double successRate = (passedTests * 100.0) / totalTests;

            sendEvent(out, "complete", Map.of(
                    "message", "Testes concluídos!",
                    "totalTests", totalTests,
                    "passedTests", passedTests,
                    "failedTests", failedTests,
                    "successRate", String.format("%.1f", successRate),
                    "progress", 100
            ));

            System.out.println("=== TESTES CONCLUÍDOS ===");
            System.out.println("Total: " + totalTests);
            System.out.println("Passou: " + passedTests);
            System.out.println("Falhou: " + failedTests);

        } catch (Exception e) {
            System.err.println("Erro durante execução: " + e.getMessage());
            e.printStackTrace();

            sendEvent(out, "error", Map.of(
                    "message", "Erro: " + e.getMessage()
            ));
        } finally {
            if (driver != null) {
                // Aguarda 3 segundos antes de fechar para o usuário ver o resultado
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                SeleniumConfig.quitDriver(driver);
            }
            out.close();
        }
    }

    /**
     * Envia um evento SSE
     */
    private void sendEvent(PrintWriter out, String eventType, Map<String, Object> data) {
        out.write("event: " + eventType + "\n");
        out.write("data: " + gson.toJson(data) + "\n\n");
        out.flush();
    }

    /**
     * Cria mapa de resultado para enviar
     */
    private Map<String, Object> createResultMap(TestResult result, int progress) {
        Map<String, Object> map = new HashMap<>();
        map.put("testName", result.getTestName());
        map.put("category", result.getCategory());
        map.put("passed", result.isPassed());
        map.put("score", result.getScore());
        map.put("details", result.getDetails());
        map.put("progress", progress);
        return map;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }
}