<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Form Monitoring - Dashboard</title>

    <!-- Bootstrap 5 -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">

    <!-- Bootstrap Icons -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">

    <!-- Chart.js -->
    <script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.0/dist/chart.umd.min.js"></script>

    <style>
        :root {
            --primary-color: #4F46E5;
            --secondary-color: #6366F1;
            --success-color: #10B981;
            --danger-color: #EF4444;
            --warning-color: #F59E0B;
            --dark-color: #1F2937;
            --light-bg: #F9FAFB;
            --border-color: #E5E7EB;
        }

        body {
            background-color: var(--light-bg);
            font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
            color: var(--dark-color);
        }

        /* Sidebar */
        .sidebar {
            position: fixed;
            top: 0;
            left: 0;
            height: 100vh;
            width: 250px;
            background: white;
            border-right: 1px solid var(--border-color);
            padding: 1.5rem 0;
            z-index: 1000;
        }

        .sidebar .brand {
            padding: 0 1.5rem 1.5rem;
            border-bottom: 1px solid var(--border-color);
            margin-bottom: 1.5rem;
        }

        .sidebar .brand h4 {
            margin: 0;
            color: var(--primary-color);
            font-weight: 700;
            font-size: 1.25rem;
        }

        .sidebar .brand small {
            color: #6B7280;
            font-size: 0.75rem;
        }

        .nav-link {
            color: #6B7280;
            padding: 0.75rem 1.5rem;
            display: flex;
            align-items: center;
            gap: 0.75rem;
            transition: all 0.2s;
            border-left: 3px solid transparent;
        }

        .nav-link:hover {
            background: var(--light-bg);
            color: var(--primary-color);
        }

        .nav-link.active {
            background: #EEF2FF;
            color: var(--primary-color);
            border-left-color: var(--primary-color);
            font-weight: 600;
        }

        .nav-link i {
            font-size: 1.25rem;
        }

        /* Main Content */
        .main-content {
            margin-left: 250px;
            padding: 2rem;
        }

        /* Header */
        .page-header {
            background: white;
            padding: 1.5rem;
            border-radius: 0.75rem;
            margin-bottom: 2rem;
            box-shadow: 0 1px 3px rgba(0,0,0,0.05);
        }

        .page-header h1 {
            margin: 0;
            font-size: 1.75rem;
            font-weight: 700;
            color: var(--dark-color);
        }

        .page-header p {
            margin: 0.5rem 0 0;
            color: #6B7280;
        }

        /* Stats Cards */
        .stat-card {
            background: white;
            border-radius: 0.75rem;
            padding: 1.5rem;
            box-shadow: 0 1px 3px rgba(0,0,0,0.05);
            border: 1px solid var(--border-color);
            transition: transform 0.2s, box-shadow 0.2s;
            min-height: 180px;
            display: flex;
            flex-direction: column;
            justify-content: space-between;
        }

        .stat-card:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(0,0,0,0.08);
        }

        .stat-card .icon {
            width: 48px;
            height: 48px;
            border-radius: 0.5rem;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 1.5rem;
            margin-bottom: 1rem;
        }

        .stat-card.primary .icon {
            background: #EEF2FF;
            color: var(--primary-color);
        }

        .stat-card.success .icon {
            background: #ECFDF5;
            color: var(--success-color);
        }

        .stat-card.danger .icon {
            background: #FEF2F2;
            color: var(--danger-color);
        }

        .stat-card.warning .icon {
            background: #FFFBEB;
            color: var(--warning-color);
        }

        .stat-card h3 {
            font-size: 2.5rem;
            font-weight: 700;
            margin: 0;
            color: var(--dark-color);
            min-height: 3rem;
            display: flex;
            align-items: center;
            justify-content: center;
        }

        .stat-card p {
            margin: 0.25rem 0 0;
            color: #6B7280;
            font-size: 0.875rem;
            min-height: 1.25rem;
        }

        .stat-card .trend {
            font-size: 0.75rem;
            padding: 0.25rem 0.5rem;
            border-radius: 0.375rem;
            margin-top: 0.5rem;
            display: inline-block;
        }

        .stat-card .trend.up {
            background: #ECFDF5;
            color: var(--success-color);
        }

        .stat-card .trend.down {
            background: #FEF2F2;
            color: var(--danger-color);
        }

        /* Chart Container */
        .chart-container {
            background: white;
            border-radius: 0.75rem;
            padding: 1.5rem;
            box-shadow: 0 1px 3px rgba(0,0,0,0.05);
            border: 1px solid var(--border-color);
        }

        .chart-container h5 {
            font-size: 1.125rem;
            font-weight: 600;
            margin-bottom: 1.5rem;
            color: var(--dark-color);
        }

        /* Test Forms Section */
        .test-forms-section {
            background: white;
            border-radius: 0.75rem;
            padding: 1.5rem;
            box-shadow: 0 1px 3px rgba(0,0,0,0.05);
            border: 1px solid var(--border-color);
        }

        .form-preview {
            border: 2px solid var(--border-color);
            border-radius: 0.5rem;
            padding: 1.5rem;
            height: 400px;
            overflow-y: auto;
            position: relative;
        }

        .form-preview.good {
            border-color: var(--success-color);
            background: #ECFDF5;
        }

        .form-preview.bad {
            border-color: var(--danger-color);
            background: #FEF2F2;
        }

        .form-preview iframe {
            width: 100%;
            height: 100%;
            border: none;
            border-radius: 0.375rem;
            background: white;
        }

        /* Test Results Table */
        .results-table {
            background: white;
            border-radius: 0.75rem;
            overflow: hidden;
            box-shadow: 0 1px 3px rgba(0,0,0,0.05);
            border: 1px solid var(--border-color);
        }

        .results-table table {
            margin: 0;
        }

        .results-table thead {
            background: var(--light-bg);
        }

        .results-table th {
            font-weight: 600;
            font-size: 0.875rem;
            color: #6B7280;
            text-transform: uppercase;
            letter-spacing: 0.5px;
            padding: 1rem;
            border-bottom: 2px solid var(--border-color);
        }

        .results-table td {
            padding: 1rem;
            vertical-align: middle;
        }

        .results-table tbody tr {
            border-bottom: 1px solid var(--border-color);
        }

        .results-table tbody tr:last-child {
            border-bottom: none;
        }

        .results-table tbody tr:hover {
            background: var(--light-bg);
        }

        /* Badges */
        .badge {
            padding: 0.375rem 0.75rem;
            font-weight: 500;
            font-size: 0.75rem;
        }

        .badge.status-pass {
            background: #ECFDF5;
            color: var(--success-color);
        }

        .badge.status-fail {
            background: #FEF2F2;
            color: var(--danger-color);
        }

        .badge.category {
            font-weight: 500;
        }

        /* Progress Bar */
        .progress {
            height: 8px;
            border-radius: 0.375rem;
            background: var(--border-color);
        }

        .progress-bar {
            border-radius: 0.375rem;
        }

        /* Buttons */
        .btn {
            padding: 0.625rem 1.25rem;
            font-weight: 500;
            border-radius: 0.5rem;
            transition: all 0.2s;
        }

        .btn-primary {
            background: var(--primary-color);
            border-color: var(--primary-color);
        }

        .btn-primary:hover {
            background: var(--secondary-color);
            border-color: var(--secondary-color);
            transform: translateY(-1px);
        }

        .btn-outline-primary {
            color: var(--primary-color);
            border-color: var(--primary-color);
        }

        .btn-outline-primary:hover {
            background: var(--primary-color);
            border-color: var(--primary-color);
        }

        /* TEST MODAL - NOVO */
        .test-modal {
            display: none;
            position: fixed;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            background: rgba(0,0,0,0.8);
            z-index: 9999;
            align-items: center;
            justify-content: center;
        }

        .test-modal.active {
            display: flex;
        }

        .test-modal-content {
            background: white;
            border-radius: 16px;
            padding: 40px;
            max-width: 700px;
            width: 90%;
            max-height: 85vh;
            overflow-y: auto;
        }

        .test-log {
            background: #1a1a1a;
            color: #0f0;
            padding: 20px;
            border-radius: 8px;
            font-family: 'Courier New', monospace;
            font-size: 13px;
            max-height: 400px;
            overflow-y: auto;
            margin-top: 20px;
        }

        .test-log-item {
            padding: 6px 0;
            border-bottom: 1px solid #333;
            animation: slideIn 0.3s ease;
        }

        @keyframes slideIn {
            from {
                opacity: 0;
                transform: translateX(-20px);
            }
            to {
                opacity: 1;
                transform: translateX(0);
            }
        }

        .test-log-item.pass {
            color: #0f0;
        }

        .test-log-item.fail {
            color: #f00;
        }

        .test-log-item.info {
            color: #0ff;
        }

        /* Responsive */
        @media (max-width: 992px) {
            .sidebar {
                transform: translateX(-100%);
            }

            .main-content {
                margin-left: 0;
            }
        }
    </style>
</head>
<body>
<!-- Sidebar -->
<div class="sidebar">
    <div class="brand">
        <h4><i class="bi bi-clipboard-data"></i> FormMonitor</h4>
        <small>v1.0</small>
    </div>

    <nav class="nav flex-column">
        <a class="nav-link active" href="#overview">
            <i class="bi bi-house-door"></i>
            <span>Visão Geral</span>
        </a>
        <a class="nav-link" href="#test-forms">
            <i class="bi bi-layout-split"></i>
            <span>Testar Formulários</span>
        </a>
        <a class="nav-link" href="#results">
            <i class="bi bi-table"></i>
            <span>Resultados</span>
        </a>
        <a class="nav-link" href="#documentation">
            <i class="bi bi-book"></i>
            <span>Documentação</span>
        </a>
        <a class="nav-link" href="${pageContext.request.contextPath}/diagnostico-completo">
            <i class="bi bi-bug"></i>
            <span>Diagnóstico</span>
        </a>
    </nav>
</div>

<!-- Main Content -->
<div class="main-content">
    <!-- Header -->
    <div class="page-header">
        <h1>Dashboard de Monitoramento</h1>
        <p>Análise automatizada de usabilidade e acessibilidade de formulários web</p>
    </div>

    <!-- Stats Cards -->
    <div class="row g-3 mb-4">
        <div class="col-lg-3 col-md-6">
            <div class="stat-card primary">
                <div class="icon">
                    <i class="bi bi-speedometer2"></i>
                </div>
                <h3>${averageScore}%</h3>
                <p>Score Médio</p>
                <c:if test="${not empty successRate}">
                    <span class="trend up">
                        <i class="bi bi-arrow-up"></i> +2.5%
                    </span>
                </c:if>
            </div>
        </div>

        <div class="col-lg-3 col-md-6">
            <div class="stat-card success">
                <div class="icon">
                    <i class="bi bi-check-circle"></i>
                </div>
                <h3>${passedTests}</h3>
                <p>Testes Aprovados</p>
                <c:if test="${not empty successRate}">
                    <span class="trend up">
                        <i class="bi bi-arrow-up"></i> ${successRate}%
                    </span>
                </c:if>
            </div>
        </div>

        <div class="col-lg-3 col-md-6">
            <div class="stat-card danger">
                <div class="icon">
                    <i class="bi bi-x-circle"></i>
                </div>
                <h3>${failedTests}</h3>
                <p>Testes Falhados</p>
                <span class="trend" style="opacity: 0;">placeholder</span>
            </div>
        </div>

        <div class="col-lg-3 col-md-6">
            <div class="stat-card warning">
                <div class="icon">
                    <i class="bi bi-clipboard-check"></i>
                </div>
                <h3>${totalTests}</h3>
                <p>Total de Testes</p>
                <span class="trend" style="opacity: 0;">placeholder</span>
            </div>
        </div>
    </div>

    <!-- Charts -->
    <div class="row g-3 mb-4">
        <div class="col-lg-8">
            <div class="chart-container">
                <h5>Resultados por Categoria</h5>
                <div style="height: 300px; position: relative;">
                    <canvas id="categoryChart"></canvas>
                </div>
            </div>
        </div>

        <div class="col-lg-4">
            <div class="chart-container">
                <h5>Taxa de Sucesso</h5>
                <div style="height: 300px; position: relative;">
                    <canvas id="successChart"></canvas>
                </div>
            </div>
        </div>
    </div>

    <!-- Test Forms Section -->
    <div class="test-forms-section mb-4" id="test-forms">
        <h5 class="mb-3">
            <i class="bi bi-layout-split"></i> Testar Formulários
        </h5>

        <div class="alert alert-info mb-4">
            <i class="bi bi-info-circle"></i>
            <strong>Como funciona:</strong> Selecione um formulário abaixo e clique em "Iniciar Teste Visual".
            Você verá o navegador abrindo e cada validação sendo executada em tempo real!
        </div>

        <div class="row g-3">
            <div class="col-md-6">
                <div class="card">
                    <div class="card-header bg-success text-white">
                        <i class="bi bi-check-circle"></i> Formulário BOM (Exemplo)
                    </div>
                    <div class="card-body">
                        <div class="form-preview good">
                            <iframe src="${pageContext.request.contextPath}/form-exemplo"></iframe>
                        </div>
                        <div class="mt-3 d-grid gap-2">
                            <button class="btn btn-success" onclick="startVisualTest('good')">
                                <i class="bi bi-play-circle"></i> Iniciar Teste Visual
                            </button>
                        </div>
                    </div>
                </div>
            </div>

            <div class="col-md-6">
                <div class="card">
                    <div class="card-header bg-danger text-white">
                        <i class="bi bi-x-circle"></i> Formulário RUIM (Exemplo)
                    </div>
                    <div class="card-body">
                        <div class="form-preview bad">
                            <iframe src="${pageContext.request.contextPath}/form-ruim"></iframe>
                        </div>
                        <div class="mt-3 d-grid gap-2">
                            <button class="btn btn-danger" onclick="startVisualTest('bad')">
                                <i class="bi bi-play-circle"></i> Iniciar Teste Visual
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Recent Results -->
    <div class="results-table mb-4" id="results">
        <div class="p-3 border-bottom">
            <h5 class="mb-0">
                <i class="bi bi-table"></i> Resultados Recentes
            </h5>
        </div>
        <div class="table-responsive">
            <table class="table table-hover mb-0">
                <thead>
                <tr>
                    <th>Teste</th>
                    <th>Categoria</th>
                    <th>Status</th>
                    <th>Score</th>
                    <th>Detalhes</th>
                    <th>Data</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="test" items="${recentTests}" varStatus="status">
                    <c:if test="${status.index < 10}">
                        <tr>
                            <td><strong>${test.testName}</strong></td>
                            <td>
                                <span class="badge category bg-primary">${test.category}</span>
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${test.passed}">
                                        <span class="badge status-pass">
                                            <i class="bi bi-check"></i> Passou
                                        </span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="badge status-fail">
                                            <i class="bi bi-x"></i> Falhou
                                        </span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <div style="min-width: 120px;">
                                    <div class="progress">
                                        <div class="progress-bar ${test.score >= 80 ? 'bg-success' : test.score >= 60 ? 'bg-warning' : 'bg-danger'}"
                                             style="width: ${test.score}%"></div>
                                    </div>
                                    <small class="text-muted">
                                        <fmt:formatNumber value="${test.score}" maxFractionDigits="1"/>%
                                    </small>
                                </div>
                            </td>
                            <td><small class="text-muted">${test.details}</small></td>
                            <td>
                                <small class="text-muted">
                                    <fmt:formatDate value="${test.timestamp}" pattern="dd/MM/yyyy HH:mm"/>
                                </small>
                            </td>
                        </tr>
                    </c:if>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
</div>

<!-- Test Modal -->
<div class="test-modal" id="testModal">
    <div class="test-modal-content">
        <h4 class="mb-4" id="modalTitle">
            <i class="bi bi-gear-fill"></i> Executando Testes em Tempo Real
        </h4>

        <div class="mb-3">
            <div class="progress" style="height: 30px;">
                <div class="progress-bar progress-bar-striped progress-bar-animated bg-primary"
                     id="progressBar"
                     style="width: 0%">
                    <strong id="progressText">0%</strong>
                </div>
            </div>
        </div>

        <div class="alert alert-info" id="statusMessage">
            <i class="bi bi-info-circle"></i>
            <strong id="statusText">Iniciando testes...</strong>
        </div>

        <div class="test-log" id="testLog">
            <div class="test-log-item info">🚀 Sistema iniciado - aguarde...</div>
        </div>

        <div class="mt-4 d-flex gap-2 justify-content-end">
            <button class="btn btn-secondary" onclick="closeModal()">
                <i class="bi bi-x-circle"></i> Fechar
            </button>
            <button class="btn btn-primary" onclick="location.reload()">
                <i class="bi bi-arrow-clockwise"></i> Atualizar Dashboard
            </button>
        </div>
    </div>
</div>

<!-- Bootstrap JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>

<script>
    // Category Chart Data
    const categoryData = {
        <c:forEach var="entry" items="${categoryStats}" varStatus="status">
        '${entry.key}': {
            total: ${entry.value.total},
            passed: ${entry.value.passed},
            avgScore: ${entry.value.avgScore}
        }<c:if test="${!status.last}">,</c:if>
        </c:forEach>
    };

    // Configuração padrão dos gráficos
    Chart.defaults.font.family = "'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif";
    Chart.defaults.color = '#6B7280';

    // Category Chart
    const ctx1 = document.getElementById('categoryChart').getContext('2d');
    new Chart(ctx1, {
        type: 'bar',
        data: {
            labels: Object.keys(categoryData),
            datasets: [{
                label: 'Aprovados',
                data: Object.values(categoryData).map(d => d.passed),
                backgroundColor: '#10B981',
                borderRadius: 4
            }, {
                label: 'Reprovados',
                data: Object.values(categoryData).map(d => d.total - d.passed),
                backgroundColor: '#EF4444',
                borderRadius: 4
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    position: 'bottom',
                    labels: {
                        padding: 15,
                        usePointStyle: true,
                        font: {
                            size: 12,
                            weight: '500'
                        }
                    }
                }
            },
            scales: {
                x: {
                    stacked: true,
                    grid: {
                        display: false
                    }
                },
                y: {
                    stacked: true,
                    beginAtZero: true,
                    grid: {
                        color: '#E5E7EB',
                        borderDash: [3, 3]
                    },
                    ticks: {
                        stepSize: 1
                    }
                }
            }
        }
    });

    // Success Rate Chart
    const ctx2 = document.getElementById('successChart').getContext('2d');
    const totalTestsChart = ${passedTests} + ${failedTests};
    new Chart(ctx2, {
        type: 'doughnut',
        data: {
            labels: ['Aprovados', 'Reprovados'],
            datasets: [{
                data: [${passedTests}, ${failedTests}],
                backgroundColor: ['#10B981', '#EF4444'],
                borderWidth: 0,
                hoverOffset: 10
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            cutout: '70%',
            plugins: {
                legend: {
                    position: 'bottom',
                    labels: {
                        padding: 15,
                        usePointStyle: true,
                        font: {
                            size: 12,
                            weight: '500'
                        }
                    }
                }
            }
        },
        plugins: [{
            id: 'centerText',
            beforeDraw: function(chart) {
                const width = chart.width;
                const height = chart.height;
                const ctx = chart.ctx;
                ctx.restore();

                const fontSize = (height / 114).toFixed(2);
                ctx.font = 'bold ' + fontSize + 'em Inter, sans-serif';
                ctx.textBaseline = 'middle';
                ctx.fillStyle = '#1F2937';

                const text = totalTestsChart > 0 ? ((${passedTests} / totalTestsChart) * 100).toFixed(0) + '%' : '0%';
                const textX = Math.round((width - ctx.measureText(text).width) / 2);
                const textY = height / 2;

                ctx.fillText(text, textX, textY);
                ctx.save();
            }
        }]
    });

    // VISUAL TEST FUNCTION - NOVO
    let eventSource = null;

    function startVisualTest(formType) {
        const modal = document.getElementById('testModal');
        const log = document.getElementById('testLog');
        const progressBar = document.getElementById('progressBar');
        const progressText = document.getElementById('progressText');
        const statusText = document.getElementById('statusText');
        const modalTitle = document.getElementById('modalTitle');

        // Limpa log anterior
        log.innerHTML = '<div class="test-log-item info">🚀 Iniciando testes...</div>';
        progressBar.style.width = '0%';
        progressText.textContent = '0%';

        // Abre modal
        modal.classList.add('active');

        // Define título
        modalTitle.innerHTML = formType === 'good'
            ? '<i class="bi bi-check-circle text-success"></i> Testando Formulário BOM'
            : '<i class="bi bi-x-circle text-danger"></i> Testando Formulário RUIM';

        // Fecha conexão anterior se existir
        if (eventSource) {
            eventSource.close();
        }

        // Cria conexão SSE
        const url = '${pageContext.request.contextPath}/executar-testes-visual?formType=' + formType;
        eventSource = new EventSource(url);

        // Evento: Status
        eventSource.addEventListener('status', function(e) {
            const data = JSON.parse(e.data);
            addLog('ℹ️ ' + data.message, 'info');
            updateProgress(data.progress, data.message);
        });

        // Evento: Teste iniciado
        eventSource.addEventListener('test-start', function(e) {
            const data = JSON.parse(e.data);
            addLog('▶️ Executando: ' + data.testName + ' (' + data.category + ')', 'info');
        });

        // Evento: Resultado do teste
        eventSource.addEventListener('test-result', function(e) {
            const data = JSON.parse(e.data);
            const status = data.passed ? '✅ PASSOU' : '❌ FALHOU';
            const cssClass = data.passed ? 'pass' : 'fail';
            const score = data.score.toFixed(1);

            addLog(status + ' - ' + data.testName + ' (' + score + '%) - ' + data.details, cssClass);
            updateProgress(data.progress, 'Teste: ' + data.testName);
        });

        // Evento: Concluído
        eventSource.addEventListener('complete', function(e) {
            const data = JSON.parse(e.data);
            addLog('', 'info');
            addLog('🎉 TESTES CONCLUÍDOS!', 'info');
            addLog('📊 Total: ' + data.totalTests + ' | Passou: ' + data.passedTests + ' | Falhou: ' + data.failedTests, 'info');
            addLog('📈 Taxa de sucesso: ' + data.successRate + '%', 'info');

            updateProgress(100, 'Concluído!');
            statusText.textContent = 'Testes concluídos! Taxa de sucesso: ' + data.successRate + '%';

            if (eventSource) {
                eventSource.close();
            }
        });

        // Evento: Erro
        eventSource.addEventListener('error', function(e) {
            try {
                const data = JSON.parse(e.data);
                addLog('❌ ERRO: ' + data.message, 'fail');
            } catch(err) {
                addLog('❌ Erro na conexão', 'fail');
            }
            eventSource.close();
        });

        // Erro de conexão
        eventSource.onerror = function() {
            addLog('❌ Erro na conexão com o servidor', 'fail');
            if (eventSource) eventSource.close();
        };
    }

    function addLog(message, type) {
        const log = document.getElementById('testLog');
        const item = document.createElement('div');
        item.className = 'test-log-item ' + type;
        item.textContent = message;
        log.appendChild(item);
        log.scrollTop = log.scrollHeight;
    }

    function updateProgress(percent, message) {
        const progressBar = document.getElementById('progressBar');
        const progressText = document.getElementById('progressText');
        const statusText = document.getElementById('statusText');

        progressBar.style.width = percent + '%';
        progressText.textContent = Math.round(percent) + '%';
        statusText.textContent = message;
    }

    function closeModal() {
        const modal = document.getElementById('testModal');
        modal.classList.remove('active');

        if (eventSource) {
            eventSource.close();
        }
    }

    // Smooth Scroll
    document.querySelectorAll('a[href^="#"]').forEach(anchor => {
        anchor.addEventListener('click', function (e) {
            e.preventDefault();
            const target = document.querySelector(this.getAttribute('href'));
            if (target) {
                target.scrollIntoView({
                    behavior: 'smooth',
                    block: 'start'
                });
            }
        });
    });
</script>
</body>
</html>