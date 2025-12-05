package br.com.formmonitoring.servlets;

import br.com.formmonitoring.dao.TestResultDAO;
import br.com.formmonitoring.model.TestResult;
import br.com.formmonitoring.util.DatabaseConnection;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;

/**
 * Servlet para diagnosticar problemas de conexão e dados
 */
@WebServlet("/diagnostico")
public class DiagnosticServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        out.println("<!DOCTYPE html>");
        out.println("<html><head><title>Diagnóstico do Sistema</title>");
        out.println("<style>body{font-family:monospace;padding:20px;background:#f0f0f0;} ");
        out.println(".success{color:green;font-weight:bold;} .error{color:red;font-weight:bold;} ");
        out.println("pre{background:white;padding:10px;border:1px solid #ccc;overflow:auto;}</style>");
        out.println("</head><body>");
        out.println("<h1>🔍 Diagnóstico do Sistema</h1>");

        // Teste 1: Conexão com banco
        out.println("<h2>1. Teste de Conexão com Banco de Dados</h2>");
        try {
            Connection conn = DatabaseConnection.getConnection();
            out.println("<p class='success'>✅ Conexão estabelecida com sucesso!</p>");
            out.println("<p>Database: " + conn.getCatalog() + "</p>");
            out.println("<p>URL: " + conn.getMetaData().getURL() + "</p>");
            conn.close();
        } catch (Exception e) {
            out.println("<p class='error'>❌ Erro na conexão:</p>");
            out.println("<pre>" + e.getMessage() + "</pre>");
            e.printStackTrace(out);
        }

        // Teste 2: Verificar se tabelas existem
        out.println("<h2>2. Verificação de Tabelas</h2>");
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            String[] tables = {"test_results", "test_schedules", "form_metrics", "test_executions"};

            for (String table : tables) {
                try {
                    ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as cnt FROM " + table);
                    if (rs.next()) {
                        int count = rs.getInt("cnt");
                        out.println("<p class='success'>✅ Tabela '" + table + "' existe - " + count + " registros</p>");
                    }
                } catch (Exception e) {
                    out.println("<p class='error'>❌ Tabela '" + table + "' não existe ou erro: " + e.getMessage() + "</p>");
                }
            }
        } catch (Exception e) {
            out.println("<p class='error'>❌ Erro ao verificar tabelas:</p>");
            out.println("<pre>" + e.getMessage() + "</pre>");
        }

        // Teste 3: Contar registros
        out.println("<h2>3. Contagem de Registros</h2>");
        TestResultDAO dao = new TestResultDAO();
        try {
            int passed = dao.countPassedTests();
            int failed = dao.countFailedTests();
            double avgScore = dao.getAverageScore();

            out.println("<p>Testes passados: <strong>" + passed + "</strong></p>");
            out.println("<p>Testes falhados: <strong>" + failed + "</strong></p>");
            out.println("<p>Score médio: <strong>" + String.format("%.2f", avgScore) + "%</strong></p>");

            if (passed == 0 && failed == 0) {
                out.println("<p class='error'>⚠️ Nenhum teste registrado no banco!</p>");
            } else {
                out.println("<p class='success'>✅ Dados encontrados no banco</p>");
            }
        } catch (Exception e) {
            out.println("<p class='error'>❌ Erro ao contar registros:</p>");
            out.println("<pre>" + e.getMessage() + "</pre>");
        }

        // Teste 4: Buscar últimos resultados
        out.println("<h2>4. Últimos 5 Resultados</h2>");
        try {
            var results = dao.getRecentResults(5);

            if (results.isEmpty()) {
                out.println("<p class='error'>⚠️ Nenhum resultado encontrado</p>");
            } else {
                out.println("<table border='1' style='border-collapse:collapse;background:white;'>");
                out.println("<tr><th>ID</th><th>Teste</th><th>Categoria</th><th>Passou</th><th>Score</th><th>Timestamp</th></tr>");

                for (TestResult r : results) {
                    out.println("<tr>");
                    out.println("<td>" + r.getId() + "</td>");
                    out.println("<td>" + r.getTestName() + "</td>");
                    out.println("<td>" + r.getCategory() + "</td>");
                    out.println("<td>" + (r.isPassed() ? "✅" : "❌") + "</td>");
                    out.println("<td>" + String.format("%.2f", r.getScore()) + "%</td>");
                    out.println("<td>" + r.getTimestamp() + "</td>");
                    out.println("</tr>");
                }

                out.println("</table>");
            }
        } catch (Exception e) {
            out.println("<p class='error'>❌ Erro ao buscar resultados:</p>");
            out.println("<pre>" + e.getMessage() + "</pre>");
        }

        // Teste 5: Inserir dado de teste
        out.println("<h2>5. Teste de Inserção</h2>");
        try {
            TestResult testResult = new TestResult();
            testResult.setTestName("Teste de Diagnóstico");
            testResult.setCategory("Sistema");
            testResult.setPassed(true);
            testResult.setScore(100.0);
            testResult.setDetails("Teste inserido via servlet de diagnóstico");
            testResult.setFormUrl("http://localhost:8080/test");
            testResult.setExecutionTime(100L);
            testResult.setTimestamp(new Timestamp(System.currentTimeMillis()));

            boolean saved = dao.save(testResult);

            if (saved) {
                out.println("<p class='success'>✅ Inserção de teste bem-sucedida! ID gerado: " + testResult.getId() + "</p>");
            } else {
                out.println("<p class='error'>❌ Falha ao inserir teste</p>");
            }
        } catch (Exception e) {
            out.println("<p class='error'>❌ Erro ao inserir teste:</p>");
            out.println("<pre>" + e.getMessage() + "</pre>");
            e.printStackTrace(out);
        }

        // Teste 6: Verificar dependências
        out.println("<h2>6. Verificação de Dependências</h2>");
        try {
            // Verificar JUnit
            Class.forName("org.junit.runner.JUnitCore");
            out.println("<p class='success'>✅ JUnit disponível</p>");
        } catch (ClassNotFoundException e) {
            out.println("<p class='error'>❌ JUnit não encontrado</p>");
        }

        try {
            // Verificar Selenium
            Class.forName("org.openqa.selenium.WebDriver");
            out.println("<p class='success'>✅ Selenium disponível</p>");
        } catch (ClassNotFoundException e) {
            out.println("<p class='error'>❌ Selenium não encontrado</p>");
        }

        try {
            // Verificar MySQL Connector
            Class.forName("com.mysql.cj.jdbc.Driver");
            out.println("<p class='success'>✅ MySQL Connector disponível</p>");
        } catch (ClassNotFoundException e) {
            out.println("<p class='error'>❌ MySQL Connector não encontrado</p>");
        }

        out.println("<hr>");
        out.println("<p><a href='" + request.getContextPath() + "/dashboard'>← Voltar ao Dashboard</a></p>");
        out.println("</body></html>");
    }
}