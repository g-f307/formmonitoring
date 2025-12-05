package br.com.formmonitoring.servlets;

import br.com.formmonitoring.dao.TestResultDAO;
import br.com.formmonitoring.model.TestResult;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Servlet para exibir o dashboard
 */
@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {

    private TestResultDAO dao = new TestResultDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // Busca resultados recentes
            List<TestResult> recentTests = dao.getRecentResults(50);

            // Busca estatísticas por categoria
            Map<String, TestResultDAO.CategoryStats> categoryStats = dao.getCategoryStatistics();

            // Calcula score médio
            double averageScore = dao.getAverageScore();

            // Conta testes passados e falhados
            int passedTests = dao.countPassedTests();
            int failedTests = dao.countFailedTests();
            int totalTests = passedTests + failedTests;

            // Calcula taxa de sucesso
            double successRate = totalTests > 0 ? (passedTests * 100.0) / totalTests : 0.0;

            // Define atributos para o JSP
            request.setAttribute("recentTests", recentTests);
            request.setAttribute("categoryStats", categoryStats);
            request.setAttribute("averageScore", String.format("%.2f", averageScore));
            request.setAttribute("passedTests", passedTests);
            request.setAttribute("failedTests", failedTests);
            request.setAttribute("totalTests", totalTests);
            request.setAttribute("successRate", String.format("%.2f", successRate));

            // Log para debug
            System.out.println("=== Dashboard Data ===");
            System.out.println("Total Tests: " + totalTests);
            System.out.println("Recent Tests: " + recentTests.size());
            System.out.println("Average Score: " + averageScore);
            System.out.println("Category Stats: " + categoryStats.size());

            // Tenta encontrar o JSP em diferentes locais
            String jspPath = "/jsp/dashboard.jsp";

            // Verifica se o JSP existe
            if (getServletContext().getResource(jspPath) != null) {
                System.out.println("JSP found at: " + jspPath);
                request.getRequestDispatcher(jspPath).forward(request, response);
            } else {
                // Tenta sem o /jsp/
                jspPath = "/dashboard.jsp";
                if (getServletContext().getResource(jspPath) != null) {
                    System.out.println("JSP found at: " + jspPath);
                    request.getRequestDispatcher(jspPath).forward(request, response);
                } else {
                    // Se não encontrar, retorna erro com informações
                    response.setContentType("text/html;charset=UTF-8");
                    response.getWriter().println("<html><body>");
                    response.getWriter().println("<h1>Erro: dashboard.jsp não encontrado</h1>");
                    response.getWriter().println("<p>Tentou: /jsp/dashboard.jsp e /dashboard.jsp</p>");
                    response.getWriter().println("<h2>Dados carregados do banco:</h2>");
                    response.getWriter().println("<ul>");
                    response.getWriter().println("<li>Total de testes: " + totalTests + "</li>");
                    response.getWriter().println("<li>Testes passados: " + passedTests + "</li>");
                    response.getWriter().println("<li>Testes falhados: " + failedTests + "</li>");
                    response.getWriter().println("<li>Score médio: " + averageScore + "%</li>");
                    response.getWriter().println("<li>Resultados recentes: " + recentTests.size() + "</li>");
                    response.getWriter().println("</ul>");
                    response.getWriter().println("<h3>Últimos testes:</h3><ul>");
                    for (TestResult test : recentTests) {
                        response.getWriter().println("<li>" + test.getTestName() + " - " +
                                (test.isPassed() ? "✅" : "❌") + " - " + test.getScore() + "%</li>");
                    }
                    response.getWriter().println("</ul>");
                    response.getWriter().println("</body></html>");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.setContentType("text/html;charset=UTF-8");
            response.getWriter().println("<html><body>");
            response.getWriter().println("<h1>Erro ao carregar dashboard</h1>");
            response.getWriter().println("<pre>" + e.getMessage() + "</pre>");
            response.getWriter().println("</body></html>");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}