# 📊 Form Monitoring System

> Sistema automatizado de análise de usabilidade, acessibilidade e performance de formulários web usando Selenium WebDriver e Java.

[![Java](https://img.shields.io/badge/Java-11-orange.svg)](https://www.oracle.com/java/)
[![Maven](https://img.shields.io/badge/Maven-3.8+-blue.svg)](https://maven.apache.org/)
[![Selenium](https://img.shields.io/badge/Selenium-4.15-green.svg)](https://www.selenium.dev/)
[![Tomcat](https://img.shields.io/badge/Tomcat-11-yellow.svg)](https://tomcat.apache.org/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue.svg)](https://www.mysql.com/)

---

## 📑 Índice

- [Visão Geral](#-visão-geral)
- [Arquitetura do Sistema](#-arquitetura-do-sistema)
- [Como Funciona](#-como-funciona)
- [Tecnologias Utilizadas](#-tecnologias-utilizadas)
- [Pré-requisitos](#-pré-requisitos)
- [Instalação](#-instalação)
- [Configuração do Banco de Dados](#-configuração-do-banco-de-dados)
- [Executando o Projeto](#-executando-o-projeto)
- [Estrutura de Testes](#-estrutura-de-testes)
- [Dashboard e Interface](#-dashboard-e-interface)
- [Validadores Implementados](#-validadores-implementados)
- [Exemplos de Uso](#-exemplos-de-uso)
- [Troubleshooting](#-troubleshooting)
- [Contribuindo](#-contribuindo)

---

## 🎯 Visão Geral

O **Form Monitoring System** é uma aplicação web Java que automatiza a análise de formulários HTML, avaliando três pilares fundamentais:

### 1. **Acessibilidade** ♿
- Labels associados aos campos
- Indicação visual de campos obrigatórios
- Atributos ARIA para leitores de tela
- Visibilidade e contraste de elementos

### 2. **Usabilidade** 🎨
- Número ideal de campos (3-10)
- Presença e clareza do botão submit
- Estrutura de mensagens de erro
- Máscaras de entrada (telefone, CEP, etc.)
- Validação de tipos de input (email, tel)
- Agrupamento lógico de campos

### 3. **Performance e Design** ⚡
- Tempo de carregamento (< 5 segundos)
- Responsividade mobile (375x667px)
- Responsividade tablet (768x1024px)
- Tamanho de fonte adequado (≥ 12px)
- Espaçamento entre campos

---

## 🏗️ Arquitetura do Sistema

```
┌─────────────────────────────────────────────────────────────┐
│                    NAVEGADOR (Cliente)                      │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐     │
│  │  Dashboard   │  │ Form Exemplo │  │  Form Ruim   │     │
│  │   (JSP)      │  │    (JSP)     │  │    (JSP)     │     │
│  └──────┬───────┘  └──────────────┘  └──────────────┘     │
└─────────┼───────────────────────────────────────────────────┘
          │
          │ HTTP Request
          │
┌─────────▼───────────────────────────────────────────────────┐
│              TOMCAT 11 (Jakarta EE)                         │
│  ┌──────────────────────────────────────────────────────┐  │
│  │                   SERVLETS                            │  │
│  │  • DashboardServlet                                   │  │
│  │  • FormExemploServlet                                 │  │
│  │  • TestExecutorServlet (SSE - Server-Sent Events)     │  │
│  │  • DiagnosticEnhancedServlet                          │  │
│  └────────────────────┬─────────────────────────────────┘  │
│                       │                                     │
│  ┌────────────────────▼─────────────────────────────────┐  │
│  │                TEST RUNNER                            │  │
│  │  • Orquestra execução dos testes                      │  │
│  │  • Gerencia categorias (Acessibilidade/Usabilidade)   │  │
│  │  • Calcula métricas e estatísticas                    │  │
│  └────────────────────┬─────────────────────────────────┘  │
│                       │                                     │
│  ┌────────────────────▼─────────────────────────────────┐  │
│  │                 VALIDATORS                            │  │
│  │  • AccessibilityValidator                             │  │
│  │  • UsabilityValidator                                 │  │
│  │  • PerformanceValidator                               │  │
│  └────────────────────┬─────────────────────────────────┘  │
│                       │                                     │
│  ┌────────────────────▼─────────────────────────────────┐  │
│  │            SELENIUM CONFIG                            │  │
│  │  • Inicializa WebDriver (Firefox/Chrome)              │  │
│  │  • Configura timeouts e opções                        │  │
│  │  • Modo headless/visual                               │  │
│  └────────────────────┬─────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
                        │
           ┌────────────┼────────────┐
           │            │            │
           ▼            ▼            ▼
    ┌──────────┐  ┌──────────┐  ┌──────────┐
    │ Firefox  │  │  Chrome  │  │  DAO     │
    │ WebDriver│  │ WebDriver│  │ (MySQL)  │
    └──────────┘  └──────────┘  └────┬─────┘
                                      │
                                      ▼
                               ┌─────────────┐
                               │   MySQL     │
                               │  Database   │
                               │test_results │
                               └─────────────┘
```

---

## 🔄 Como Funciona

### **Fluxo Completo de Execução**

#### **1. Inicialização do Dashboard**
```
Usuário acessa → /dashboard
                      ↓
            DashboardServlet.doGet()
                      ↓
            Consulta TestResultDAO
                      ↓
        Busca métricas do banco de dados:
        • Total de testes
        • Taxa de sucesso
        • Resultados recentes
        • Estatísticas por categoria
                      ↓
            Renderiza dashboard.jsp
```

#### **2. Execução de Testes (Modo Visual)**

Quando o usuário clica em "Iniciar Teste Visual":

```javascript
// Frontend (dashboard.jsp)
startVisualTest('good') // ou 'bad'
        ↓
EventSource conecta em /executar-testes-visual
        ↓
// Backend (TestExecutorServlet)
Recebe parâmetro formType = 'good' ou 'bad'
        ↓
Monta URL do formulário:
• good → /form-exemplo
• bad  → /form-ruim
        ↓
Inicializa WebDriver (SeleniumConfig)
        ↓
┌─────────────────────────────────┐
│   LOOP DE VALIDAÇÕES            │
│                                 │
│  Para cada Validator:           │
│  1. UsabilityValidator          │
│     • validateFieldCount()      │
│     • validateSubmitButton()    │
│     • validateErrorMessages()   │
│     • validateInputMasks()      │
│     • validateEmailValidation() │
│     • validateFieldGrouping()   │
│                                 │
│  2. AccessibilityValidator      │
│     • validateAssociatedLabels()│
│     • validateRequiredFields()  │
│                                 │
│  Para cada teste:               │
│  ┌───────────────────────────┐ │
│  │ 1. Abre URL no navegador  │ │
│  │ 2. Localiza elementos     │ │
│  │ 3. Valida regra           │ │
│  │ 4. Calcula score (0-100%) │ │
│  │ 5. Cria TestResult        │ │
│  │ 6. Salva no banco (DAO)   │ │
│  │ 7. Envia evento SSE       │ │
│  └───────────────────────────┘ │
│          ↓                      │
│  Frontend recebe evento:        │
│  • Adiciona linha no log        │
│  • Atualiza barra de progresso  │
│  • Mostra status (✅/❌)        │
└─────────────────────────────────┘
        ↓
Todos os testes concluídos
        ↓
Envia evento 'complete' com resumo:
• Total de testes
• Testes passados/falhados
• Taxa de sucesso
        ↓
Fecha WebDriver
        ↓
Frontend exibe "Testes Concluídos!"
```

#### **3. Estrutura de um Validador**

Vamos analisar o `validateFieldCount()` como exemplo:

```java
public TestResult validateFieldCount(WebDriver driver, String formUrl) {
    long startTime = System.currentTimeMillis(); // ⏱️ Inicia cronômetro
    
    try {
        // 1️⃣ NAVEGA até o formulário
        driver.get(formUrl);
        Thread.sleep(1000); // Aguarda carregamento
        
        // 2️⃣ LOCALIZA todos os campos visíveis (exceto botões)
        List<WebElement> campos = driver.findElements(
            By.cssSelector("input:not([type='submit']):not([type='button']):not([type='hidden']), select, textarea")
        );
        
        int totalCampos = campos.size();
        
        // 3️⃣ APLICA REGRA DE NEGÓCIO
        // Ideal: 3-10 campos
        boolean passou = totalCampos >= 3 && totalCampos <= 10;
        
        // 4️⃣ CALCULA SCORE PONDERADO
        double score;
        if (totalCampos >= 4 && totalCampos <= 8) {
            score = 100.0; // Perfeito
        } else if (totalCampos >= 3 && totalCampos <= 10) {
            score = 85.0;  // Aceitável
        } else if (totalCampos > 10) {
            score = Math.max(50.0, 100.0 - (totalCampos - 10) * 5); // Penaliza excesso
        } else {
            score = 60.0;  // Poucos campos
        }
        
        // 5️⃣ CRIA RESULTADO
        return createTestResult(
            "Número Ideal de Campos",
            "Usabilidade",
            passou,
            score,
            String.format("Formulário possui %d campos (ideal: 3-10)", totalCampos),
            formUrl,
            System.currentTimeMillis() - startTime // ⏱️ Tempo de execução
        );
        
    } catch (Exception e) {
        // 6️⃣ TRATAMENTO DE ERRO
        return createErrorResult("Número Ideal de Campos", "Usabilidade", formUrl, e);
    }
}
```

---

## 🛠️ Tecnologias Utilizadas

### **Backend**
| Tecnologia | Versão | Propósito |
|------------|--------|-----------|
| Java | 11 | Linguagem principal |
| Jakarta Servlet API | 6.0.0 | API de Servlets (Tomcat 11) |
| Maven | 3.8+ | Gerenciamento de dependências |
| MySQL Connector | 8.0.33 | Driver JDBC |
| Selenium WebDriver | 4.15.0 | Automação de navegador |
| WebDriverManager | 5.6.2 | Gerenciamento automático de drivers |
| Gson | 2.10.1 | Serialização JSON |
| Logback | 1.4.11 | Sistema de logs |

### **Frontend**
| Tecnologia | Propósito |
|------------|-----------|
| JSP (Jakarta Server Pages) | Templates dinâmicos |
| Bootstrap 5.3.2 | Framework CSS |
| Chart.js 4.4.0 | Gráficos interativos |
| Bootstrap Icons | Ícones |
| Vanilla JavaScript | Interatividade |

### **Automação de Testes**
| Tecnologia | Propósito |
|------------|-----------|
| JUnit 5 (Jupiter) | Framework de testes |
| Selenium WebDriver | Controle do navegador |
| Firefox/Chrome WebDriver | Execução dos testes |

---

## ✅ Pré-requisitos

### **Obrigatórios**

1. **Java Development Kit (JDK) 11+**
   ```bash
   java -version
   # Deve exibir: java version "11.x.x" ou superior
   ```

2. **Apache Maven 3.8+**
   ```bash
   mvn -version
   # Deve exibir: Apache Maven 3.8.x
   ```

3. **MySQL 8.0+**
   ```bash
   mysql --version
   # Deve exibir: mysql Ver 8.0.x
   ```

4. **Apache Tomcat 11** (ou será baixado automaticamente pelo Maven Cargo)

5. **Navegador Web**
   - **Firefox** (recomendado) - [Download](https://www.mozilla.org/firefox/)
   - OU **Chrome** - [Download](https://www.google.com/chrome/)

### **Opcional**
- IDE Java (IntelliJ IDEA, Eclipse, VS Code)
- Git (para clonar o repositório)

---

## 📥 Instalação

### **Passo 1: Clone o Repositório**

```bash
git clone https://github.com/seu-usuario/form-monitoring.git
cd form-monitoring
```

### **Passo 2: Configure o Banco de Dados**

Edite `src/main/java/br/com/formmonitoring/util/DatabaseConnection.java`:

```java
private static final String URL = "jdbc:mysql://localhost:3306/form_monitoring";
private static final String USER = "root";
private static final String PASSWORD = "SUA_SENHA_AQUI"; // ⚠️ ALTERE AQUI
```

### **Passo 3: Configure o WebDriver**

Edite `src/main/java/br/com/formmonitoring/config/SeleniumConfig.java`:

```java
private static final String BROWSER = "firefox"; // ou "chrome"
private static final boolean HEADLESS = false;   // true = sem interface, false = com interface visual
```

---

## 🗄️ Configuração do Banco de Dados

### **1. Criar o Banco de Dados**

```sql
CREATE DATABASE form_monitoring CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE form_monitoring;
```

### **2. Criar a Tabela de Resultados**

```sql
CREATE TABLE test_results (
    id INT AUTO_INCREMENT PRIMARY KEY,
    test_name VARCHAR(255) NOT NULL,
    category VARCHAR(100) NOT NULL,
    passed BOOLEAN NOT NULL,
    score DOUBLE NOT NULL,
    details TEXT,
    form_url VARCHAR(500),
    execution_time BIGINT,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_category (category),
    INDEX idx_timestamp (timestamp),
    INDEX idx_passed (passed)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

### **3. Verificar a Criação**

```sql
DESCRIBE test_results;
SELECT COUNT(*) FROM test_results;
```

---

## 🚀 Executando o Projeto

### **Modo 1: Build Rápido (sem testes de integração)**

```bash
# Compila e empacota o WAR
mvn clean package

# Deploy no Tomcat (manual)
# Copie target/form-monitoring.war para TOMCAT_HOME/webapps/
```

### **Modo 2: Com Servidor Embutido (Recomendado)**

```bash
# Limpa, compila e inicia Tomcat automaticamente
mvn clean package cargo:run
```

Acesse: **http://localhost:8080/form-monitoring/dashboard**

### **Modo 3: Executar Testes de Integração**

```bash
# Executa testes + inicia/para Tomcat automaticamente
mvn clean verify -Pintegration-tests
```

### **Modo 4: Build sem Nenhum Teste**

```bash
mvn clean package -Pno-tests
```

---

## 🧪 Estrutura de Testes

### **Testes Unitários (Surefire)**

Executados durante `mvn test` - **NÃO requerem servidor rodando**:

```bash
mvn test
```

Localização: `src/test/java/br/com/formmonitoring/tests/`

- `FormAccessibilityTest.java` - 4 testes
- `FormUsabilityTest.java` - 6 testes
- `FormPerformanceTest.java` - 6 testes

### **Testes de Integração (Failsafe)**

Executados durante `mvn verify` - **REQUEREM servidor rodando**:

```bash
# Maven inicia Tomcat automaticamente antes dos testes
mvn verify -Pintegration-tests
```

### **Perfis Maven Disponíveis**

```bash
# 1. Build padrão (rápido, sem testes de integração)
mvn clean package

# 2. Build sem nenhum teste
mvn clean package -Pno-tests

# 3. Build com testes de integração
mvn clean verify -Pintegration-tests

# 4. Apenas testes de integração (pula unitários)
mvn verify -Ponly-integration
```

---

## 📊 Dashboard e Interface

### **Componentes do Dashboard**

#### **1. Cards de Métricas**
```
┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐
│  Score Médio    │  │ Testes Aprovados│  │ Testes Falhados │  │  Total Testes   │
│     85.3%       │  │       45        │  │       12        │  │       57        │
└─────────────────┘  └─────────────────┘  └─────────────────┘  └─────────────────┘
```

#### **2. Gráficos Interativos (Chart.js)**

- **Gráfico de Barras**: Resultados por categoria (Acessibilidade, Usabilidade, Performance)
- **Gráfico de Pizza**: Taxa de sucesso geral

#### **3. Seção de Teste em Tempo Real**

```
┌────────────────────────────────────────────────────────┐
│  FORMULÁRIO BOM          │  FORMULÁRIO RUIM            │
│  [Preview do formulário] │  [Preview do formulário]    │
│                          │                             │
│  [Iniciar Teste Visual]  │  [Iniciar Teste Visual]     │
└────────────────────────────────────────────────────────┘
```

Ao clicar em "Iniciar Teste Visual", um modal é aberto:

```
┌──────────────────────────────────────────────────────────┐
│  🧪 Executando Testes em Tempo Real                     │
│                                                          │
│  [██████████████████████░░░░░░░░] 85%                  │
│                                                          │
│  Status: Executando teste de Usabilidade...             │
│                                                          │
│  LOG DE EXECUÇÃO:                                        │
│  ┌────────────────────────────────────────────────────┐ │
│  │ 🚀 Sistema iniciado - aguarde...                   │ │
│  │ ▶️ Executando: Número Ideal de Campos             │ │
│  │ ✅ PASSOU - Número Ideal de Campos (100%)         │ │
│  │ ▶️ Executando: Botão Submit Visível               │ │
│  │ ✅ PASSOU - Botão Submit Visível (100%)           │ │
│  │ ...                                                │ │
│  └────────────────────────────────────────────────────┘ │
│                                                          │
│  [Fechar]  [Atualizar Dashboard]                        │
└──────────────────────────────────────────────────────────┘
```

#### **4. Tabela de Resultados Recentes**

| Teste | Categoria | Status | Score | Detalhes | Data |
|-------|-----------|--------|-------|----------|------|
| Labels Associados | Acessibilidade | ✅ Passou | 95% | 5/5 inputs com labels | 05/12 14:30 |
| Validação Email | Validação | ❌ Falhou | 20% | Campo com type incorreto | 05/12 14:31 |

---

## 🔍 Validadores Implementados

### **AccessibilityValidator**

#### 1. `validateAssociatedLabels()`
```java
// O que faz:
// - Localiza todos os inputs do formulário
// - Verifica se cada input tem um <label> associado via atributo 'for'
// - OU se o input está dentro de um <label>

// Critérios de aprovação:
// ✅ Score ≥ 70% → Passou
// ❌ Score < 70% → Falhou

// Exemplo:
// ✅ BOM: <label for="nome">Nome</label> <input id="nome">
// ❌ RUIM: Nome: <input> (sem label associado)
```

#### 2. `validateRequiredFieldsIndication()`
```java
// O que faz:
// - Localiza campos com atributo 'required'
// - Verifica se há indicação visual (* ou texto "obrigatório")
// - Verifica presença de aria-required="true"

// Critérios:
// ✅ Score ≥ 60% → Passou
// ❌ Score < 60% → Falhou
```

#### 3. `validateARIAAttributes()`
```java
// O que faz:
// - Verifica presença de:
//   • aria-label
//   • aria-describedby
//   • aria-required
//   • etc.

// Critérios:
// ✅ Score ≥ 30% → Passou (mais flexível)
```

#### 4. `validateElementVisibility()`
```java
// O que faz:
// - Verifica se todos os campos estão visíveis (display != none)

// Critérios:
// ✅ Score ≥ 90% → Passou
```

---

### **UsabilityValidator**

#### 1. `validateFieldCount()`
```java
// Regra: Formulário ideal tem entre 3-10 campos

// Pontuação:
// • 4-8 campos: 100 pontos (perfeito)
// • 3 ou 9-10 campos: 85 pontos (aceitável)
// • 11+ campos: penaliza 5 pontos por campo extra
// • < 3 campos: 60 pontos

// Por quê?
// - Muitos campos = usuário desiste
// - Poucos campos = pode faltar informação importante
```

#### 2. `validateSubmitButton()`
```java
// O que faz:
// - Localiza botões com type="submit"
// - Verifica se estão visíveis
// - Verifica se têm texto descritivo

// Pontuação:
// • Botão visível + texto > 5 chars: 100 pontos
// • Botão visível + texto > 2 chars: 80 pontos
// • Sem botão visível: 0 pontos
```

#### 3. `validateErrorMessages()`
```java
// O que faz:
// - Busca containers para mensagens de erro:
//   .error, .error-message, .invalid-feedback, etc.

// Pontuação:
// • 1 container por campo: 100 pontos
// • Metade dos campos com container: 90 pontos
// • Poucos containers: 70 pontos
// • Nenhum container: 0 pontos
```

#### 4. `validateInputMasks()`
```java
// O que faz:
// - Verifica se campos de texto têm:
//   • placeholder
//   • pattern (regex de validação)
//   • maxlength/minlength

// Exemplo:
// ✅ BOM: <input type="tel" placeholder="(00) 00000-0000" maxlength="15">
// ❌ RUIM: <input type="text" name="telefone">
```

#### 5. `validateEmailValidation()`
```java
// O que faz:
// - Verifica se campos de email usam type="email"
// - Detecta campos de email com type="text" (erro comum)

// Busca por:
// • name="email"
// • id="email"
// • placeholder contendo "@"

// Pontuação:
// ✅ Todos os campos email com type correto: 100 pontos
// ❌ Algum campo email com type="text": 20 pontos
```

#### 6. `validateFieldGrouping()`
```java
// O que faz:
// - Verifica uso de <fieldset> para agrupar campos
// - Verifica uso de divs com classes como .form-group

// Por quê?
// - Agrupamento lógico melhora a compreensão
// - Facilita navegação por teclado

// Critérios:
// ✅ Score ≥ 60% → Passou
```

---

### **PerformanceValidator**

#### 1. `validateLoadingTime()`
```java
// O que faz:
// - Mede tempo desde driver.get() até página estar carregada

// Limites:
// • ≤ 5000ms (5s): 100 pontos
// • > 5000ms: penaliza progressivamente

// Técnica:
long startTime = System.currentTimeMillis();
driver.get(formUrl);
driver.findElement(By.tagName("body")); // Aguarda body estar presente
long loadTime = System.currentTimeMillis() - startTime;
```

#### 2. `validateMobileResponsiveness()`
```java
// O que faz:
// - Redimensiona janela para 375x667 (iPhone SE)
// - Verifica se todos os campos continuam visíveis

// Pontuação:
// • Todos os campos visíveis: 100 pontos
// • Alguns campos ocultos: proporcional
```

#### 3. `validateTabletResponsiveness()`
```java
// Similar ao mobile, mas com 768x1024 (iPad)
```

#### 4. `validateFontSize()`
```java
// O que faz:
// - Lê font-size de labels e inputs via CSS
// - Verifica se ≥ 12px

// Técnica:
String fontSize = element.getCssValue("font-size");
double size = Double.parseDouble(fontSize.replace("px", ""));
if (size >= 12) { /* OK */ }
```

#### 5. `validateFieldSpacing()`
```java
// O que faz:
// - Verifica margin-bottom e padding-bottom dos campos
// - Verifica espaçamento do elemento pai

// Por quê?
// - Campos muito próximos dificultam o clique
// - Principalmente em mobile (dedo > cursor)
```

---

## 💡 Exemplos de Uso

### **Exemplo 1: Testar Formulário Existente**

```bash
# 1. Acesse o dashboard
http://localhost:8080/form-monitoring/dashboard

# 2. Role até "Testar Formulários"

# 3. Clique em "Iniciar Teste Visual" no formulário desejado

# 4. Observe a execução em tempo real:
#    - Navegador abrirá
#    - Cada teste será executado visivelmente
#    - Log mostrará resultados instantaneamente

# 5. Após conclusão, clique em "Atualizar Dashboard"
#    para ver as novas métricas
```

### **Exemplo 2: Executar Testes via TestRunner**

```java
// TestRunner.java - método main()
public static void main(String[] args) {
    System.out.println("=== Executando Todos os Testes ===\n");
    
    TestExecutionResult result = runAllTests();
    
    System.out.println("\n=== RESULTADOS FINAIS ===");
    System.out.println("Total de testes: " + result.getTotalTests());
    System.out.println("Testes passados: " + result.getPassedTests());
    System.out.println("Testes falhados: " + result.getFailedTests());
    System.out.printf("Taxa de sucesso: %.2f%%\n", result.getSuccessRate());
}
```

Execute:
```bash
cd form-monitoring
mvn exec:java -Dexec.mainClass="br.com.formmonitoring.runner.TestRunner"
```

### **Exemplo 3: Criar Novo Validador**

```java
// 1. Crie uma classe em src/main/java/.../validators/
public class CustomValidator {
    
    public TestResult validateCustomRule(WebDriver driver, String formUrl) {
        long startTime = System.currentTimeMillis();
        
        try {
            driver.get(formUrl);
            
            // Sua lógica de validação aqui
            List<WebElement> elementos = driver.findElements(By.cssSelector(".minha-classe"));
            
            boolean passou = elementos.size() > 0;
            double score = passou ? 100.0 : 0.0;
            
            return createTestResult(
                "Meu Teste Customizado",
                "Custom",
                passou,
                score,
                "Detalhes do resultado",
                formUrl,
                System.currentTimeMillis() - startTime
            );
            
        } catch (Exception e) {
            return createErrorResult("Meu Teste", "Custom", formUrl, e);
        }
    }
}

// 2. Adicione ao TestRunner.java
public static TestExecutionResult runCustomTests() {
    TestExecutionResult result = new TestExecutionResult();
    CustomValidator validator = new CustomValidator();
    WebDriver driver = SeleniumConfig.getDriver();
    
    TestResult r1 = validator.validateCustomRule(driver, FORM_URL);
    dao.save(r1);
    result.addTestResult(r1);
    
    SeleniumConfig.quitDriver(driver);
    return result;
}
```

---

## 🐛 Troubleshooting

### **Problema 1: WebDriver não encontrado**

**Erro:**
```
SessionNotCreatedException: Could not start a new session
```

**Solução:**
```bash
# O WebDriverManager deve baixar automaticamente, mas se falhar:

# Firefox:
# 1. Baixe geckodriver em: https://github.com/mozilla/geckodriver/releases
# 2. Adicione ao PATH do sistema

# Chrome:
# 1. Baixe chromedriver em: https://chromedriver.chromium.org/
# 2. Adicione ao PATH do sistema
```

---

### **Problema 2: Erro de conexão com MySQL**

**Erro:**
```
SQLException: Access denied for user 'root'@'localhost'
```

**Solução:**
```java
// Verifique DatabaseConnection.java
private static final String PASSWORD = "SUA_SENHA_CORRETA";

// Teste a conexão:
mysql -u root -p
USE form_monitoring;
```

---

### **Problema 3: Página 404 ao acessar /dashboard**

**Causa:** Context path incorreto ou Tomcat não iniciou

**Solução:**
```bash
# Verifique se o Tomcat está rodando:
netstat -an | grep 8080

# Verifique o WAR foi deployado:
ls TOMCAT_HOME/webapps/form-monitoring.war

# Tente acessar:
http://localhost:8080/form-monitoring/dashboard
# Não: http://localhost:8080/dashboard
```

---

### **Problema 4: Testes não salvam no banco**

**Diagnóstico:**
```bash
# Acesse o diagnóstico completo:
http://localhost:8080/form-monitoring/diagnostico-completo

# Verifique:
# 1. Conexão com banco ✅
# 2. Estrutura da tabela ✅
# 3. Dados no banco ❌ <- Problema aqui
```

**Soluções:**
1. Verifique se `TestResultDAO.save()` retorna `true`
2. Verifique logs do console para `SQLException`
3. Execute teste manual no servlet de diagnóstico

---

### **Problema 5: Modal de teste fica travado em "Iniciando..."**

**Causa:** Servlet não está enviando eventos SSE

**Solução:**
```javascript
// Verifique console do navegador (F12):
// Deve mostrar: EventSource connected to /executar-testes-visual

// Se não aparecer:
// 1. Verifique se TestExecutorServlet está registrado
// 2. Verifique logs do Tomcat
// 3. Teste diretamente: http://localhost:8080/form-monitoring/executar-testes-visual?formType=good
```

---

### **Problema 6: Firefox abre mas não executa testes**

**Causa:** Versão incompatível do Firefox ou geckodriver

**Solução:**
```bash
# Atualize o Firefox para a versão mais recente

# OU force o Chrome:
# Em SeleniumConfig.java:
private static final String BROWSER = "chrome";
```

---

### **Problema 7: Build Maven falha**

**Erro:**
```
Failed to execute goal on project form-monitoring: 
Could not resolve dependencies
```

**Solução:**
```bash
# Limpe o cache do Maven:
mvn dependency:purge-local-repository

# Force update:
mvn clean install -U

# Se persistir, delete .m2/repository e tente novamente:
rm -rf ~/.m2/repository
mvn clean install
```

---

## 📁 Estrutura do Projeto

```
form-monitoring/
│
├── pom.xml                          # Configuração Maven + plugins
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── br/com/formmonitoring/
│   │   │       ├── config/
│   │   │       │   └── SeleniumConfig.java         # Configuração WebDriver
│   │   │       │
│   │   │       ├── dao/
│   │   │       │   └── TestResultDAO.java          # Acesso ao banco
│   │   │       │
│   │   │       ├── model/
│   │   │       │   └── TestResult.java             # Modelo de dados
│   │   │       │
│   │   │       ├── runner/
│   │   │       │   └── TestRunner.java             # Orquestrador de testes
│   │   │       │
│   │   │       ├── servlets/
│   │   │       │   ├── DashboardServlet.java       # Dashboard principal
│   │   │       │   ├── FormExemploServlet.java     # Formulário bom
│   │   │       │   ├── FormRuimServlet.java        # Formulário ruim
│   │   │       │   ├── TestExecutorServlet.java    # Execução com SSE
│   │   │       │   └── DiagnosticEnhancedServlet.java # Diagnóstico
│   │   │       │
│   │   │       ├── util/
│   │   │       │   └── DatabaseConnection.java     # Conexão MySQL
│   │   │       │
│   │   │       └── validators/
│   │   │           ├── AccessibilityValidator.java # Validações A11Y
│   │   │           ├── UsabilityValidator.java     # Validações UX
│   │   │           └── PerformanceValidator.java   # Validações perf.
│   │   │
│   │   ├── resources/
│   │   │   └── logback.xml                         # Configuração de logs
│   │   │
│   │   └── webapp/
│   │       ├── WEB-INF/
│   │       │   └── web.xml                         # Configuração Servlet
│   │       │
│   │       ├── css/
│   │       │   └── dashboard.css                   # Estilos (legado)
│   │       │
│   │       └── jsp/
│   │           ├── dashboard.jsp                   # Dashboard principal
│   │           ├── form-exemplo.jsp                # Formulário BOM
│   │           └── form-ruim.jsp                   # Formulário RUIM
│   │
│   └── test/
│       └── java/
│           └── br/com/formmonitoring/tests/
│               ├── FormAccessibilityTest.java      # Testes A11Y (JUnit)
│               ├── FormUsabilityTest.java          # Testes UX (JUnit)
│               ├── FormPerformanceTest.java        # Testes perf. (JUnit)
│               └── FormMonitoringTestSuite.java    # Suite completa
│
├── .gitignore
├── mvnw                              # Maven Wrapper (Unix)
├── mvnw.cmd                          # Maven Wrapper (Windows)
└── README.md                         # Este arquivo
```

---

## 🎓 Conceitos Avançados

### **Server-Sent Events (SSE)**

O projeto usa SSE para comunicação em tempo real entre servidor e cliente:

```java
// Backend (TestExecutorServlet.java)
response.setContentType("text/event-stream");
response.setHeader("Cache-Control", "no-cache");

PrintWriter out = response.getWriter();
out.write("event: status\n");
out.write("data: {\"message\":\"Teste iniciado\"}\n\n");
out.flush(); // ⚠️ Crucial: envia imediatamente
```

```javascript
// Frontend (dashboard.jsp)
const eventSource = new EventSource('/executar-testes-visual?formType=good');

eventSource.addEventListener('status', function(e) {
    const data = JSON.parse(e.data);
    console.log(data.message);
});

eventSource.addEventListener('test-result', function(e) {
    const data = JSON.parse(e.data);
    addLog(data.testName + ': ' + (data.passed ? '✅' : '❌'));
});

eventSource.addEventListener('complete', function(e) {
    console.log('Testes concluídos!');
    eventSource.close(); // Fecha conexão
});
```

### **Por que SSE e não WebSocket?**

- **SSE**: Unidirecional (servidor → cliente) - Perfeito para logs de progresso
- **WebSocket**: Bidirecional - Desnecessário aqui, mais complexo

---

### **Maven Cargo Plugin**

O Cargo permite iniciar/parar Tomcat automaticamente:

```xml
<plugin>
    <groupId>org.codehaus.cargo</groupId>
    <artifactId>cargo-maven3-plugin</artifactId>
    <executions>
        <execution>
            <id>start-container</id>
            <phase>pre-integration-test</phase> <!-- Antes dos testes -->
            <goals><goal>start</goal></goals>
        </execution>
        <execution>
            <id>stop-container</id>
            <phase>post-integration-test</phase> <!-- Depois dos testes -->
            <goals><goal>stop</goal></goals>
        </execution>
    </executions>
</plugin>
```

**Vantagens:**
- CI/CD automático
- Testes isolados
- Sem interferência manual

---

### **Padrão DAO (Data Access Object)**

```java
// TestResultDAO.java encapsula TODA a lógica de banco
public class TestResultDAO {
    
    // Salvar resultado
    public boolean save(TestResult result) {
        String sql = "INSERT INTO test_results (...) VALUES (...)";
        // ... código JDBC
    }
    
    // Buscar resultados
    public List<TestResult> getRecentResults(int limit) {
        String sql = "SELECT * FROM test_results ORDER BY timestamp DESC LIMIT ?";
        // ... código JDBC
    }
    
    // Estatísticas
    public Map<String, CategoryStats> getCategoryStatistics() {
        String sql = "SELECT category, COUNT(*), AVG(score) FROM test_results GROUP BY category";
        // ... código JDBC
    }
}
```

**Benefícios:**
- ✅ Separação de responsabilidades
- ✅ Fácil de testar (mock do DAO)
- ✅ Mudanças no banco não afetam lógica de negócio

---

### **Strategy Pattern nos Validators**

Cada validador implementa a mesma interface implícita:

```java
TestResult validateX(WebDriver driver, String formUrl);
```

Isso permite:

```java
// Lista de validators
List<Validator> validators = Arrays.asList(
    new AccessibilityValidator(),
    new UsabilityValidator(),
    new PerformanceValidator()
);

// Executar todos dinamicamente
for (Validator v : validators) {
    TestResult result = v.validate(driver, url);
    dao.save(result);
}
```

---

## 🔐 Segurança e Boas Práticas

### **1. Nunca commite senhas**

```java
// ❌ NUNCA FAÇA ISSO:
private static final String PASSWORD = "minhasenha123";

// ✅ MELHOR:
private static final String PASSWORD = System.getenv("DB_PASSWORD");

// OU use arquivo de propriedades:
Properties props = new Properties();
props.load(new FileInputStream("config.properties"));
String password = props.getProperty("db.password");
```

### **2. Prepared Statements (proteção contra SQL Injection)**

```java
// ✅ CORRETO (já implementado):
String sql = "SELECT * FROM test_results WHERE category = ?";
PreparedStatement stmt = conn.prepareStatement(sql);
stmt.setString(1, category); // Escapa automaticamente

// ❌ VULNERÁVEL:
String sql = "SELECT * FROM test_results WHERE category = '" + category + "'";
// Permite SQL injection: category = "'; DROP TABLE test_results; --"
```

### **3. Timeout nos testes Selenium**

```java
// ✅ Configurado (SeleniumConfig.java):
driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(20));

// Evita testes travados indefinidamente
```

---

## 📈 Métricas e KPIs

### **Score de Qualidade**

O sistema calcula scores de 0-100% para cada teste:

```java
// Exemplo: validateFieldCount()
if (campos >= 4 && campos <= 8) {
    score = 100.0;  // Perfeito
} else if (campos >= 3 && campos <= 10) {
    score = 85.0;   // Bom
} else if (campos > 10) {
    score = Math.max(50.0, 100.0 - (campos - 10) * 5); // Penaliza excesso
} else {
    score = 60.0;   // Ruim
}
```

### **Agregação de Resultados**

```sql
-- Score médio por categoria
SELECT 
    category,
    AVG(score) as avg_score,
    COUNT(*) as total_tests,
    SUM(CASE WHEN passed = 1 THEN 1 ELSE 0 END) as passed_tests
FROM test_results
GROUP BY category;
```

### **Taxa de Sucesso Geral**

```
Taxa de Sucesso = (Testes Passados / Total de Testes) × 100%
```

---

## 🚀 Roadmap Futuro

### **Funcionalidades Planejadas**

- [ ] **Testes de Contraste de Cores** (WCAG AA/AAA)
- [ ] **Validação de Navigation** (Tab order, Skip links)
- [ ] **Testes de Cross-Browser** (Safari, Edge)
- [ ] **Relatórios em PDF** (export de resultados)
- [ ] **API REST** para integração externa
- [ ] **Agendamento de Testes** (Quartz Scheduler)
- [ ] **Notificações** (email/Slack quando testes falham)
- [ ] **Histórico de Métricas** (gráfico de evolução ao longo do tempo)
- [ ] **Suporte a múltiplos idiomas** (i18n)
- [ ] **Testes de Segurança** (CSRF, XSS em forms)

---

## 📖 Recursos Adicionais

### **Artigos Relacionados**
- [WCAG 2.1 Guidelines](https://www.w3.org/WAI/WCAG21/quickref/)
- [Web Form Design Best Practices](https://uxdesign.cc/web-form-design-best-practices-9e09f5e63d8)
- [Selenium with Java Tutorial](https://www.selenium.dev/documentation/webdriver/)

### **Ferramentas Complementares**
- [Lighthouse](https://developers.google.com/web/tools/lighthouse) - Auditoria automatizada
- [axe DevTools](https://www.deque.com/axe/devtools/) - Testes de acessibilidade
- [WAVE](https://wave.webaim.org/) - Web Accessibility Evaluation Tool

---

