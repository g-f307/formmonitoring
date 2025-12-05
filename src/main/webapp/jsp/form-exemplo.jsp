<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Formulário de Exemplo - BOM</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: Arial, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            display: flex;
            justify-content: center;
            align-items: center;
            padding: 20px;
        }

        .form-container {
            background: white;
            padding: 40px;
            border-radius: 12px;
            box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
            max-width: 600px;
            width: 100%;
        }

        h1 {
            color: #667eea;
            margin-bottom: 10px;
            font-size: 2em;
        }

        .subtitle {
            color: #666;
            margin-bottom: 30px;
        }

        .form-group {
            margin-bottom: 25px;
        }

        label {
            display: block;
            margin-bottom: 8px;
            font-weight: 600;
            color: #333;
            font-size: 16px;
        }

        label .required {
            color: #f44336;
            margin-left: 4px;
        }

        input[type="text"],
        input[type="email"],
        input[type="tel"],
        select,
        textarea {
            width: 100%;
            padding: 12px;
            border: 2px solid #ddd;
            border-radius: 8px;
            font-size: 16px;
            transition: border-color 0.3s;
        }

        input:focus,
        select:focus,
        textarea:focus {
            outline: none;
            border-color: #667eea;
        }

        textarea {
            resize: vertical;
            min-height: 100px;
        }

        .error-message {
            color: #f44336;
            font-size: 14px;
            margin-top: 5px;
            display: none;
        }

        .input-hint {
            color: #666;
            font-size: 13px;
            margin-top: 5px;
        }

        button[type="submit"] {
            background: linear-gradient(135deg, #667eea, #764ba2);
            color: white;
            padding: 15px 40px;
            border: none;
            border-radius: 8px;
            font-size: 18px;
            font-weight: 600;
            cursor: pointer;
            width: 100%;
            transition: transform 0.3s;
        }

        button[type="submit"]:hover {
            transform: translateY(-2px);
        }

        .success-message {
            background: #4caf50;
            color: white;
            padding: 15px;
            border-radius: 8px;
            margin-bottom: 20px;
            display: none;
        }

        @media (max-width: 600px) {
            .form-container {
                padding: 20px;
            }

            h1 {
                font-size: 1.5em;
            }
        }
    </style>
</head>
<body>
<div class="form-container">
    <h1>📝 Formulário de Contato</h1>
    <p class="subtitle">Exemplo de formulário com boa usabilidade</p>

    <div id="successMessage" class="success-message">
        ✅ Formulário enviado com sucesso!
    </div>

    <form id="contactForm" novalidate>
        <div class="form-group">
            <label for="nome">
                Nome Completo
                <span class="required" aria-label="obrigatório">*</span>
            </label>
            <input
                    type="text"
                    id="nome"
                    name="nome"
                    required
                    aria-required="true"
                    aria-describedby="nomeHint"
                    placeholder="Digite seu nome completo"
            >
            <div id="nomeHint" class="input-hint">Insira seu nome e sobrenome</div>
            <div class="error-message" id="nomeError">Por favor, insira seu nome completo</div>
        </div>

        <div class="form-group">
            <label for="email">
                E-mail
                <span class="required" aria-label="obrigatório">*</span>
            </label>
            <input
                    type="email"
                    id="email"
                    name="email"
                    required
                    aria-required="true"
                    aria-describedby="emailHint"
                    placeholder="seu@email.com"
                    pattern="[a-z0-9._%+-]+@[a-z0-9.-]+\.[a-z]{2,}$"
            >
            <div id="emailHint" class="input-hint">Usaremos para responder sua mensagem</div>
            <div class="error-message" id="emailError">Por favor, insira um e-mail válido</div>
        </div>

        <div class="form-group">
            <label for="telefone">
                Telefone
                <span class="required" aria-label="obrigatório">*</span>
            </label>
            <input
                    type="tel"
                    id="telefone"
                    name="telefone"
                    required
                    aria-required="true"
                    placeholder="(00) 00000-0000"
                    maxlength="15"
            >
            <div class="error-message" id="telefoneError">Por favor, insira um telefone válido</div>
        </div>

        <div class="form-group">
            <label for="assunto">
                Assunto
                <span class="required" aria-label="obrigatório">*</span>
            </label>
            <select
                    id="assunto"
                    name="assunto"
                    required
                    aria-required="true"
            >
                <option value="">Selecione um assunto</option>
                <option value="duvida">Dúvida</option>
                <option value="sugestao">Sugestão</option>
                <option value="reclamacao">Reclamação</option>
                <option value="elogio">Elogio</option>
            </select>
            <div class="error-message" id="assuntoError">Por favor, selecione um assunto</div>
        </div>

        <div class="form-group">
            <label for="mensagem">
                Mensagem
                <span class="required" aria-label="obrigatório">*</span>
            </label>
            <textarea
                    id="mensagem"
                    name="mensagem"
                    required
                    aria-required="true"
                    placeholder="Digite sua mensagem aqui..."
                    minlength="10"
            ></textarea>
            <div class="error-message" id="mensagemError">Por favor, insira uma mensagem com pelo menos 10 caracteres</div>
        </div>

        <button type="submit">Enviar Mensagem</button>
    </form>
</div>

<script>
    // Máscara de telefone
    document.getElementById('telefone').addEventListener('input', function(e) {
        let value = e.target.value.replace(/\D/g, '');
        if (value.length <= 11) {
            value = value.replace(/^(\d{2})(\d)/g, '($1) $2');
            value = value.replace(/(\d)(\d{4})$/, '$1-$2');
            e.target.value = value;
        }
    });

    // Validação em tempo real
    const form = document.getElementById('contactForm');
    const fields = ['nome', 'email', 'telefone', 'assunto', 'mensagem'];

    fields.forEach(field => {
        const input = document.getElementById(field);
        const error = document.getElementById(field + 'Error');

        input.addEventListener('blur', function() {
            if (!this.validity.valid) {
                error.style.display = 'block';
                this.style.borderColor = '#f44336';
            } else {
                error.style.display = 'none';
                this.style.borderColor = '#4caf50';
            }
        });

        input.addEventListener('input', function() {
            if (this.validity.valid) {
                error.style.display = 'none';
                this.style.borderColor = '#ddd';
            }
        });
    });

    // Submit
    form.addEventListener('submit', function(e) {
        e.preventDefault();

        let isValid = true;
        fields.forEach(field => {
            const input = document.getElementById(field);
            const error = document.getElementById(field + 'Error');

            if (!input.validity.valid) {
                error.style.display = 'block';
                input.style.borderColor = '#f44336';
                isValid = false;
            }
        });

        if (isValid) {
            document.getElementById('successMessage').style.display = 'block';
            form.reset();

            // Reseta cores dos campos
            fields.forEach(field => {
                document.getElementById(field).style.borderColor = '#ddd';
            });

            setTimeout(() => {
                document.getElementById('successMessage').style.display = 'none';
            }, 5000);
        }
    });
</script>
</body>
</html>