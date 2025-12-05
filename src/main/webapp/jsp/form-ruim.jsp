<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <title>Formulário Ruim - Exemplo</title>
    <style>
        /* ❌ MÁS PRÁTICAS INTENCIONAIS PARA DEMONSTRAÇÃO */

        body {
            font-family: Times New Roman, serif; /* Fonte desatualizada */
            background: #f5f5f5;
            padding: 10px;
        }

        .container {
            max-width: 500px;
            margin: 0 auto;
            background: white;
            padding: 15px; /* Pouco padding */
            border: 1px solid #ccc;
        }

        h1 {
            font-size: 16px; /* Título muito pequeno */
            color: #888; /* Contraste insuficiente */
            margin-bottom: 10px;
        }

        /* ❌ Labels não associados aos inputs */
        label {
            display: inline; /* Não em bloco */
            font-size: 11px; /* Fonte muito pequena */
            color: #999; /* Baixo contraste */
        }

        /* ❌ Inputs sem espaçamento adequado */
        input, select, textarea {
            width: 100%;
            padding: 4px; /* Padding mínimo */
            margin: 2px 0; /* Pouco espaçamento */
            border: 1px solid #ddd;
            font-size: 11px; /* Fonte muito pequena */
        }

        /* ❌ Sem indicação de campos obrigatórios */
        /* ❌ Sem feedback visual de erro */

        button {
            background: #aaa; /* Cor fraca */
            color: white;
            padding: 5px 10px; /* Padding pequeno */
            border: none;
            font-size: 10px; /* Fonte muito pequena */
            cursor: pointer;
            margin-top: 5px;
        }

        button:hover {
            background: #999;
        }

        /* ❌ Responsive ruim */
        @media (max-width: 600px) {
            /* Sem ajustes para mobile */
        }
    </style>
</head>
<body>
<div class="container">
    <h1>Contato</h1>

    <!-- ❌ Form sem atributos de acessibilidade -->
    <form>
        <!-- ❌ Label não associado ao input (sem 'for') -->
        <label>Nome</label>
        <!-- ❌ Sem 'id', sem 'required', sem 'aria-*' -->
        <input type="text" name="nome" placeholder="nome">

        <!-- ❌ Label não associado -->
        <label>Email</label>
        <!-- ❌ type="text" ao invés de type="email" -->
        <!-- ❌ Sem validação -->
        <input type="text" name="email" placeholder="email">

        <!-- ❌ Label não associado -->
        <label>Tel</label>
        <!-- ❌ Sem máscara, sem pattern -->
        <input type="text" name="tel">

        <!-- ❌ Label não associado -->
        <label>Assunto</label>
        <!-- ❌ Sem option padrão, sem required -->
        <select name="assunto">
            <option value="1">Duvida</option>
            <option value="2">Reclamacao</option>
            <option value="3">Elogio</option>
        </select>

        <!-- ❌ Label não associado -->
        <label>Mensagem</label>
        <!-- ❌ Sem minlength, sem required -->
        <textarea name="msg"></textarea>

        <!-- ❌ Botão sem texto descritivo -->
        <button type="submit">OK</button>
    </form>
</div>

<!-- ❌ Sem validação JavaScript -->
<!-- ❌ Sem feedback de erro -->
<!-- ❌ Sem máscaras de entrada -->
</body>
</html>