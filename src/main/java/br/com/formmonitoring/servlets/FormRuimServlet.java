package br.com.formmonitoring.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Servlet para exibir o formulário RUIM (exemplo de más práticas)
 * Rota: /formmonitoring/form-ruim
 */
@WebServlet({"/form-ruim", "/formulario-ruim"})
public class FormRuimServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Encaminha para o JSP
        request.getRequestDispatcher("/jsp/form-ruim.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        doGet(request, response);
    }
}