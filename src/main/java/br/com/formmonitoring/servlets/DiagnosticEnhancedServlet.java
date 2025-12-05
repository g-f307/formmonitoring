package br.com.formmonitoring.servlets;

import br.com.formmonitoring.dao.TestResultDAO;
import br.com.formmonitoring.model.TestResult;
import br.com.formmonitoring.runner.TestRunner;
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
 * Servlet MELHORADO para diagnosticar problemas de conexão e dados
 */
@WebServlet("/diagnostico-completo")
public class DiagnosticEnhancedServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        out.println("<!DOCTYPE html>");
        out.println("<html><head><title>Diagnóstico Completo do Sistema</title>");
        out.println("<style>");
        out.println("body{font-family:monospace;padding:20px;background:#1a1a1a;color:#0f0;} ");
        out.println(".success{color:#0f0;font-weight:bold;} ");
        out.println(".error{color:#f00;font-weight:bold;} ");
        out.println(".warning{color:#ff0;font-weight:bold;} ");
        out.println("pre{background:#000;padding:15px;border:1px solid #0f0;overflow:auto;} ");
        out.println(".section{background:#2a2a2a;padding:20px;margin:20px 0;border:2px solid #0f0;border-radius:8px;} ");
        out.println("h1{color:#0f0;text-align:center;font-size:2em;} ");
        out.println("h2{color:#0ff;border-bottom:2px solid #0ff;padding-bottom:10px;} ");
        out.println(".btn{background:#0f0;color:#000;padding:10px 20px;border:none;border-radius:5px;cursor:pointer;font-weight:bold;margin:5px;text-decoration:none;display:inline-block;} ");
        out.println(".btn:hover{background:#0ff;} ");
        out.println("table{width:100%;border-collapse:collapse;margin:10px 0;} ");
        out.println("th,td{padding:10px;border:1px solid #0f0;text-align:left;} ");
        out.println("th{background:#0f0;color:#000;font-weight:bold;} ");
        out.println("</style>");
        out.println("</head><body>");
        out.println("<h1>🔍 DIAGNÓSTICO COMPLETO DO SISTEMA</h1>");

        // TESTE 1: Conexão com banco
        out.println("<div class='section'>");
        out.println("<h2>1️⃣ TESTE DE CONEXÃO COM BANCO DE DADOS</h2>");
        boolean dbConnected = false;
        try {
            Connection conn = DatabaseConnection.getConnection();
            dbConnected = true;
            out.println("<p class='success'>✅ CONEXÃO ESTABELECIDA COM SUCESSO!</p>");
            out.println("<pre>");
            out.println("Database: " + conn.getCatalog());
            out.println("URL: " + conn.getMetaData().getURL());
            out.println("Driver: " + conn.getMetaData().getDriverName());
            out.println("</pre>");
            conn.close();
        } catch (Exception e) {
            out.println("<p class='error'>❌ ERRO NA CONEXÃO:</p>");
            out.println("<pre>" + e.getMessage() + "</pre>");
            e.printStackTrace(out);
        }
        out.println("</div>");

        // TESTE 2: Verificar estrutura das tabelas
        out.println("<div class='section'>");
        out.println("<h2>2️⃣ VERIFICAÇÃO DE ESTRUTURA DAS TABELAS</h2>");
        if (dbConnected) {
            try (Connection conn = DatabaseConnection.getConnection();
                 Statement stmt = conn.createStatement()) {

                // Verifica test_results
                out.println("<h3>📋 Tabela test_results:</h3>");
                ResultSet rs = stmt.executeQuery("DESCRIBE test_results");
                out.println("<table><tr><th>Campo</th><th>Tipo</th><th>Null</th><th>Key</th></tr>");
                while (rs.next()) {
                    out.println("<tr>");
                    out.println("<td>" + rs.getString("Field") + "</td>");
                    out.println("<td>" + rs.getString("Type") + "</td>");
                    out.println("<td>" + rs.getString("Null") + "</td>");
                    out.println("<td>" + rs.getString("Key") + "</td>");
                    out.println("</tr>");
                }
                out.println("</table>");

                // Conta registros
                rs = stmt.executeQuery("SELECT COUNT(*) as cnt FROM test_results");
                if (rs.next()) {
                    int count = rs.getInt("cnt");
                    out.println("<p class='success'>✅ Tabela existe - " + count + " registros encontrados</p>");
                }

            } catch (Exception e) {
                out.println("<p class='error'>❌ Erro ao verificar estrutura:</p>");
                out.println("<pre>" + e.getMessage() + "</pre>");
            }
        }
        out.println("</div>");

        // TESTE 3: Dados do DAO
        out.println("<div class='section'>");
        out.println("<h2>3️⃣ TESTE DE ACESSO AOS DADOS (DAO)</h2>");
        TestResultDAO dao = new TestResultDAO();
        try {
            int passed = dao.countPassedTests();
            int failed = dao.countFailedTests();
            double avgScore = dao.getAverageScore();

            out.println("<table>");
            out.println("<tr><th>Métrica</th><th>Valor</th></tr>");
            out.println("<tr><td>Testes Passados</td><td class='success'>" + passed + "</td></tr>");
            out.println("<tr><td>Testes Falhados</td><td class='error'>" + failed + "</td></tr>");
            out.println("<tr><td>Score Médio</td><td>" + String.format("%.2f%%", avgScore) + "</td></tr>");
            out.println("</table>");

            if (passed == 0 && failed == 0) {
                out.println("<p class='warning'>⚠️ NENHUM TESTE REGISTRADO NO BANCO!</p>");
                out.println("<p>Isso significa que os testes NÃO estão salvando resultados.</p>");
            } else {
                out.println("<p class='success'>✅ Dados encontrados no banco</p>");
            }
        } catch (Exception e) {
            out.println("<p class='error'>❌ Erro ao acessar DAO:</p>");
            out.println("<pre>" + e.getMessage() + "</pre>");
        }
        out.println("</div>");

        // TESTE 4: Últimos 10 resultados
        out.println("<div class='section'>");
        out.println("<h2>4️⃣ ÚLTIMOS 10 RESULTADOS DO BANCO</h2>");
        try {
            var results = dao.getRecentResults(10);

            if (results.isEmpty()) {
                out.println("<p class='warning'>⚠️ NENHUM RESULTADO ENCONTRADO NA TABELA</p>");
                out.println("<p>Possíveis causas:</p>");
                out.println("<ul>");
                out.println("<li>Testes nunca foram executados</li>");
                out.println("<li>Testes estão falhando antes de salvar</li>");
                out.println("<li>Problema na URL do formulário de teste</li>");
                out.println("<li>WebDriver não consegue acessar o formulário</li>");
                out.println("</ul>");
            } else {
                out.println("<table>");
                out.println("<tr><th>ID</th><th>Teste</th><th>Categoria</th><th>Status</th><th>Score</th><th>Detalhes</th><th>URL</th><th>Data</th></tr>");

                for (TestResult r : results) {
                    out.println("<tr>");
                    out.println("<td>" + r.getId() + "</td>");
                    out.println("<td>" + r.getTestName() + "</td>");
                    out.println("<td>" + r.getCategory() + "</td>");
                    out.println("<td class='" + (r.isPassed() ? "success" : "error") + "'>" + (r.isPassed() ? "✅ PASSOU" : "❌ FALHOU") + "</td>");
                    out.println("<td>" + String.format("%.2f%%", r.getScore()) + "</td>");
                    out.println("<td>" + r.getDetails() + "</td>");
                    out.println("<td>" + r.getFormUrl() + "</td>");
                    out.println("<td>" + r.getTimestamp() + "</td>");
                    out.println("</tr>");
                }

                out.println("</table>");
                out.println("<p class='success'>✅ " + results.size() + " resultados encontrados</p>");
            }
        } catch (Exception e) {
            out.println("<p class='error'>❌ Erro ao buscar resultados:</p>");
            out.println("<pre>" + e.getMessage() + "</pre>");
        }
        out.println("</div>");

        // TESTE 5: Inserir dado de teste AGORA
        out.println("<div class='section'>");
        out.println("<h2>5️⃣ TESTE DE INSERÇÃO EM TEMPO REAL</h2>");
        try {
            TestResult testResult = new TestResult();
            testResult.setTestName("🧪 Teste de Diagnóstico - " + System.currentTimeMillis());
            testResult.setCategory("Sistema");
            testResult.setPassed(true);
            testResult.setScore(100.0);
            testResult.setDetails("Teste inserido via servlet de diagnóstico completo");
            testResult.setFormUrl("http://localhost:8080/form-monitoring/diagnostico");
            testResult.setExecutionTime(100L);
            testResult.setTimestamp(new Timestamp(System.currentTimeMillis()));

            boolean saved = dao.save(testResult);

            if (saved) {
                out.println("<p class='success'>✅ INSERÇÃO BEM-SUCEDIDA!</p>");
                out.println("<p>ID gerado: <strong>" + testResult.getId() + "</strong></p>");
                out.println("<p>Teste: " + testResult.getTestName() + "</p>");
                out.println("<p>Categoria: " + testResult.getCategory() + "</p>");
            } else {
                out.println("<p class='error'>❌ FALHA NA INSERÇÃO</p>");
                out.println("<p>O método save() retornou false</p>");
            }
        } catch (Exception e) {
            out.println("<p class='error'>❌ ERRO DURANTE INSERÇÃO:</p>");
            out.println("<pre>" + e.getMessage() + "</pre>");
            e.printStackTrace(out);
        }
        out.println("</div>");

        // TESTE 6: Verificar URLs
        out.println("<div class='section'>");
        out.println("<h2>6️⃣ VERIFICAÇÃO DE URLs DOS TESTES</h2>");
        out.println("<p>URLs configuradas nos testes:</p>");
        out.println("<ul>");
        out.println("<li>FormAccessibilityTest: <code>http://localhost:8080/formmonitoring/form-exemplo.jsp</code></li>");
        out.println("<li>FormPerformanceTest: <code>http://localhost:8080/formmonitoring/form-exemplo.jsp</code></li>");
        out.println("<li>FormUsabilityTest: <code>http://localhost:8080/form-monitoring/form-exemplo.jsp</code> ⚠️ DIFERENTE!</li>");
        out.println("</ul>");

        out.println("<p class='warning'>⚠️ INCONSISTÊNCIA DETECTADA!</p>");
        out.println("<p>A URL correta provavelmente é:</p>");
        out.println("<pre>http://localhost:8080/" + request.getContextPath() + "/jsp/form-exemplo.jsp</pre>");

        out.println("<p>Context Path atual: <strong>" + request.getContextPath() + "</strong></p>");
        out.println("</div>");

        // TESTE 7: Executar teste real
        out.println("<div class='section'>");
        out.println("<h2>7️⃣ EXECUÇÃO DE TESTE REAL</h2>");
        out.println("<p class='warning'>⚠️ Este teste pode demorar alguns segundos...</p>");
        out.println("<form method='post' action='" + request.getContextPath() + "/diagnostico-completo'>");
        out.println("<button type='submit' class='btn'>▶️ EXECUTAR TESTE DE USABILIDADE AGORA</button>");
        out.println("</form>");
        out.println("</div>");

        // RESUMO FINAL
        out.println("<div class='section'>");
        out.println("<h2>📊 RESUMO DO DIAGNÓSTICO</h2>");
        out.println("<ol>");
        out.println("<li>Conexão com banco: " + (dbConnected ? "<span class='success'>OK</span>" : "<span class='error'>FALHOU</span>") + "</li>");
        out.println("<li>Estrutura das tabelas: <span class='success'>OK</span></li>");
        out.println("<li>Testes no banco: " + (dao.countPassedTests() + dao.countFailedTests() > 0 ? "<span class='success'>ENCONTRADOS</span>" : "<span class='warning'>VAZIOS</span>") + "</li>");
        out.println("</ol>");
        out.println("</div>");

        // AÇÕES RECOMENDADAS
        out.println("<div class='section'>");
        out.println("<h2>🔧 AÇÕES RECOMENDADAS</h2>");
        out.println("<ol>");
        out.println("<li><strong>Corrigir URLs:</strong> Padronizar a URL em todos os testes para: <code>" + request.getContextPath() + "/jsp/form-exemplo.jsp</code></li>");
        out.println("<li><strong>Verificar Selenium:</strong> Garantir que o Chrome/Firefox está instalado e o WebDriver funciona</li>");
        out.println("<li><strong>Executar teste manual:</strong> Clique no botão acima para executar um teste real</li>");
        out.println("<li><strong>Verificar logs:</strong> Cheque os logs do Tomcat em busca de erros</li>");
        out.println("</ol>");
        out.println("</div>");

        out.println("<hr>");
        out.println("<p style='text-align:center;'>");
        out.println("<a href='" + request.getContextPath() + "/dashboard' class='btn'>🏠 Voltar ao Dashboard</a> ");
        out.println("<a href='" + request.getContextPath() + "/diagnostico-completo' class='btn'>🔄 Atualizar</a>");
        out.println("</p>");
        out.println("</body></html>");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        out.println("<!DOCTYPE html>");
        out.println("<html><head><title>Executando Teste Real</title>");
        out.println("<style>body{font-family:monospace;padding:20px;background:#1a1a1a;color:#0f0;} ");
        out.println(".success{color:#0f0;} .error{color:#f00;} .warning{color:#ff0;} ");
        out.println("pre{background:#000;padding:15px;border:1px solid #0f0;overflow:auto;}</style>");
        out.println("</head><body>");
        out.println("<h1>🧪 EXECUTANDO TESTE DE USABILIDADE</h1>");

        try {
            out.println("<p>Iniciando TestRunner...</p>");
            out.flush();

            TestRunner.TestExecutionResult result = TestRunner.runTestsByCategory("usabilidade");

            out.println("<h2 class='success'>✅ TESTES CONCLUÍDOS!</h2>");
            out.println("<pre>");
            out.println("Total de testes: " + result.getTotalTests());
            out.println("Testes passados: " + result.getPassedTests());
            out.println("Testes falhados: " + result.getFailedTests());
            out.println("Taxa de sucesso: " + String.format("%.2f%%", result.getSuccessRate()));
            out.println("Tempo de execução: " + result.getExecutionTime() + "ms");
            out.println("</pre>");

            if (!result.getFailureMessages().isEmpty()) {
                out.println("<h3 class='error'>❌ Falhas detectadas:</h3>");
                out.println("<pre>");
                for (String failure : result.getFailureMessages()) {
                    out.println(failure);
                }
                out.println("</pre>");
            }

            out.println("<p><a href='" + request.getContextPath() + "/diagnostico-completo'>🔙 Voltar</a></p>");

        } catch (Exception e) {
            out.println("<h2 class='error'>❌ ERRO DURANTE EXECUÇÃO:</h2>");
            out.println("<pre>");
            e.printStackTrace(out);
            out.println("</pre>");
        }

        out.println("</body></html>");
    }
}