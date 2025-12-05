<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard - Monitoramento de Formulários</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/dashboard.css">
    <script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.0/dist/chart.umd.min.js"></script>
</head>
<body>
<div class="container">
    <header class="header">
        <h1>📊 Dashboard de Usabilidade de Formulários</h1>
        <p class="subtitle">Monitoramento automatizado de qualidade e acessibilidade</p>
    </header>

    <!-- Painel de Controle -->
    <div class="control-panel">
        <button id="runAllTests" class="btn btn-primary">
            ▶️ Executar Todos os Testes
        </button>
        <button id="runAccessibility" class="btn btn-secondary">
            ♿ Acessibilidade
        </button>
        <button id="runUsability" class="btn btn-secondary">
            👤 Usabilidade
        </button>
        <button id="runPerformance" class="btn btn-secondary">
            ⚡ Performance
        </button>
        <button id="refreshDashboard" class="btn btn-info">
            🔄 Atualizar Dashboard
        </button>
    </div>

    <!-- Status de Execução -->
    <div id="executionStatus" class="execution-status hidden">
        <div class="spinner"></div>
        <span id="statusText">Executando testes...</span>
    </div>

    <!-- Métricas Principais -->
    <div class="metrics-grid">
        <div class="metric-card">
            <div class="metric-icon">📈</div>
            <h3>Score Médio</h3>
            <div class="metric-value">${averageScore}%</div>
            <div class="metric-label">Qualidade Geral</div>
        </div>

        <div class="metric-card">
            <div class="metric-icon">✅</div>
            <h3>Taxa de Sucesso</h3>
            <div class="metric-value">${successRate}%</div>
            <div class="metric-label">${passedTests}/${totalTests} Testes</div>
        </div>

        <div class="metric-card">
            <div class="metric-icon">🧪</div>
            <h3>Testes Executados</h3>
            <div class="metric-value">${totalTests}</div>
            <div class="metric-label">Total de Verificações</div>
        </div>

        <div class="metric-card">
            <div class="metric-icon">❌</div>
            <h3>Testes Falhados</h3>
            <div class="metric-value">${failedTests}</div>
            <div class="metric-label">Necessitam Correção</div>
        </div>
    </div>

    <!-- Gráfico de Categorias -->
    <div class="chart-section">
        <h2>📊 Resultados por Categoria</h2>
        <div class="chart-container">
            <canvas id="categoryChart"></canvas>
        </div>
    </div>

    <!-- Comparação Bom vs Ruim -->
    <div class="comparison-section">
        <h2>🔍 Formulário BOM vs RUIM</h2>
        <div class="comparison-grid">
            <div class="form-example good">
                <div class="example-header">
                    <span class="badge badge-success">✅ FORMULÁRIO BOM</span>
                </div>
                <ul class="checklist">
                    <li>✓ 100% dos inputs com labels associados</li>
                    <li>✓ Campos obrigatórios claramente indicados</li>
                    <li>✓ Validação em tempo real</li>
                    <li>✓ Mensagens de erro específicas e próximas aos campos</li>
                    <li>✓ Responsivo em mobile e tablet</li>
                    <li>✓ Contraste adequado (WCAG AA)</li>
                    <li>✓ Atributos ARIA para tecnologias assistivas</li>
                    <li>✓ Tempo de carregamento < 3 segundos</li>
                    <li>✓ Fonte legível (≥ 14px)</li>
                    <li>✓ Espaçamento adequado entre campos</li>
                </ul>
            </div>

            <div class="form-example bad">
                <div class="example-header">
                    <span class="badge badge-danger">❌ FORMULÁRIO RUIM</span>
                </div>
                <ul class="checklist">
                    <li>✗ Labels ausentes ou não associados</li>
                    <li>✗ Sem indicação visual de obrigatoriedade</li>
                    <li>✗ Validação apenas no submit</li>
                    <li>✗ Mensagens de erro genéricas</li>
                    <li>✗ Não responsivo (quebra em mobile)</li>
                    <li>✗ Contraste insuficiente</li>
                    <li>✗ Faltam atributos de acessibilidade</li>
                    <li>✗ Carregamento lento (> 5 segundos)</li>
                    <li>✗ Fonte muito pequena (< 12px)</li>
                    <li>✗ Campos colados sem espaçamento</li>
                </ul>
            </div>
        </div>
    </div>

    <!-- Tabela de Resultados Recentes -->
    <div class="results-section">
        <h2>📋 Resultados Recentes</h2>
        <div class="table-container">
            <table class="results-table">
                <thead>
                <tr>
                    <th>Teste</th>
                    <th>Categoria</th>
                    <th>Status</th>
                    <th>Score</th>
                    <th>Detalhes</th>
                    <th>Tempo de Execução</th>
                    <th>Data/Hora</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="test" items="${recentTests}">
                    <tr>
                        <td class="test-name">${test.testName}</td>
                        <td>
                            <span class="category-badge ${test.category}">${test.category}</span>
                        </td>
                        <td>
                            <c:choose>
                                <c:when test="${test.passed}">
                                    <span class="badge badge-success">✅ Passou</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="badge badge-danger">❌ Falhou</span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td>
                            <div class="score-container">
                                <div class="score-bar">
                                    <div class="score-fill" style="width: ${test.score}%; background-color: ${test.score >= 80 ? '#4caf50' : test.score >= 60 ? '#ff9800' : '#f44336'}"></div>
                                </div>
                                <span class="score-value"><fmt:formatNumber value="${test.score}" maxFractionDigits="1"/>%</span>
                            </div>
                        </td>
                        <td class="details">${test.details}</td>
                        <td><fmt:formatNumber value="${test.executionTime}" groupingUsed="true"/> ms</td>
                        <td class="timestamp">
                            <fmt:formatDate value="${test.timestamp}" pattern="dd/MM/yyyy HH:mm:ss"/>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
</div>

<script>
    // Dados para o gráfico
    const categoryData = {
        <c:forEach var="entry" items="${categoryStats}" varStatus="status">
        '${entry.key}': {
            total: ${entry.value.total},
            passed: ${entry.value.passed},
            avgScore: ${entry.value.avgScore}
        }<c:if test="${!status.last}">,</c:if>
        </c:forEach>
    };

    // Configuração do gráfico
    const ctx = document.getElementById('categoryChart').getContext('2d');
    const labels = Object.keys(categoryData);
    const passedData = labels.map(cat => categoryData[cat].passed);
    const failedData = labels.map(cat => categoryData[cat].total - categoryData[cat].passed);

    new Chart(ctx, {
        type: 'bar',
        data: {
            labels: labels,
            datasets: [
                {
                    label: 'Testes Passados',
                    data: passedData,
                    backgroundColor: 'rgba(76, 175, 80, 0.8)',
                    borderColor: 'rgba(76, 175, 80, 1)',
                    borderWidth: 1
                },
                {
                    label: 'Testes Falhados',
                    data: failedData,
                    backgroundColor: 'rgba(244, 67, 54, 0.8)',
                    borderColor: 'rgba(244, 67, 54, 1)',
                    borderWidth: 1
                }
            ]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            scales: {
                y: {
                    beginAtZero: true,
                    ticks: {
                        stepSize: 1
                    }
                }
            },
            plugins: {
                legend: {
                    display: true,
                    position: 'top'
                },
                title: {
                    display: true,
                    text: 'Distribuição de Resultados por Categoria'
                }
            }
        }
    });

    // Event listeners
    document.getElementById('runAllTests').addEventListener('click', () => executeTests(null));
    document.getElementById('runAccessibility').addEventListener('click', () => executeTests('acessibilidade'));
    document.getElementById('runUsability').addEventListener('click', () => executeTests('usabilidade'));
    document.getElementById('runPerformance').addEventListener('click', () => executeTests('performance'));
    document.getElementById('refreshDashboard').addEventListener('click', () => location.reload());

    function executeTests(category) {
        const statusDiv = document.getElementById('executionStatus');
        const statusText = document.getElementById('statusText');

        statusDiv.classList.remove('hidden');
        statusText.textContent = category ?
            `Executando testes de ${category}...` :
            'Executando todos os testes...';

        const url = '${pageContext.request.contextPath}/executar-testes' +
            (category ? '?category=' + category : '');

        fetch(url, { method: 'POST' })
            .then(response => response.json())
            .then(data => {
                statusText.textContent = `Testes concluídos! ${data.passedTests}/${data.totalTests} passados`;
                setTimeout(() => {
                    statusDiv.classList.add('hidden');
                    location.reload();
                }, 2000);
            })
            .catch(error => {
                statusText.textContent = 'Erro ao executar testes: ' + error;
                setTimeout(() => statusDiv.classList.add('hidden'), 3000);
            });
    }
</script>
</body>
</html>