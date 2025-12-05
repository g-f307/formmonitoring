package br.com.formmonitoring.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Servlet para exibir o formulário de exemplo
 * Agora você pode acessar via: /formmonitoring/form-exemplo
 */
@WebServlet({"/form-exemplo", "/formulario"}) // Múltiplas rotas possíveis!
public class FormExemploServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Encaminha para o JSP
        request.getRequestDispatcher("/jsp/form-exemplo.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Se houver processamento de formulário, faça aqui
        // Por enquanto, apenas exibe o formulário novamente
        doGet(request, response);
    }
}