package br.com.formmonitoring.servlets;

import br.com.formmonitoring.runner.TestRunner;
import com.google.gson.Gson;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Servlet para executar testes de usabilidade
 */
@WebServlet("/executar-testes")
public class FormTestServlet extends HttpServlet {

    private Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();

        try {
            String category = request.getParameter("category");

            TestRunner.TestExecutionResult result;

            if (category != null && !category.isEmpty()) {
                // Executa testes de uma categoria específica
                result = TestRunner.runTestsByCategory(category);
            } else {
                // Executa todos os testes
                result = TestRunner.runAllTests();
            }

            // Prepara resposta JSON
            Map<String, Object> jsonResponse = new HashMap<>();
            jsonResponse.put("success", result.isSuccess());
            jsonResponse.put("totalTests", result.getTotalTests());
            jsonResponse.put("passedTests", result.getPassedTests());
            jsonResponse.put("failedTests", result.getFailedTests());
            jsonResponse.put("successRate", result.getSuccessRate());
            jsonResponse.put("executionTime", result.getExecutionTime());
            jsonResponse.put("categoryResults", result.getCategoryResults());
            jsonResponse.put("failures", result.getFailureMessages());

            String json = gson.toJson(jsonResponse);
            out.print(json);

        } catch (Exception e) {
            e.printStackTrace();

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());

            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.toJson(errorResponse));
        } finally {
            out.flush();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }
}