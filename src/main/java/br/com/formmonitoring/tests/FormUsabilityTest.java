package br.com.formmonitoring.tests;

import br.com.formmonitoring.config.SeleniumConfig;
import br.com.formmonitoring.dao.TestResultDAO;
import br.com.formmonitoring.model.TestResult;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.sql.Timestamp;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Testes de usabilidade para formulários
 * VERSÃO CORRIGIDA
 */
public class FormUsabilityTest {

    private WebDriver driver;
    private TestResultDAO dao;
    // CORRIGIDO: URL padronizada
    private String formUrl = "http://localhost:8080/form-monitoring/jsp/form-exemplo.jsp";
    private long startTime;

    @Before
    public void setUp() {
        try {
            driver = SeleniumConfig.getDriver();
            dao = new TestResultDAO();
            startTime = System.currentTimeMillis();
            System.out.println("=== Iniciando testes de Usabilidade ===");
        } catch (Exception e) {
            System.err.println("Erro ao inicializar teste: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void testNumeroIdealCampos() {
        try {
            System.out.println("Executando: testNumeroIdealCampos");
            driver.get(formUrl);

            // Aguarda a página carregar
            Thread.sleep(1000);

            List<WebElement> campos = driver.findElements(
                    By.cssSelector("input:not([type='submit']):not([type='button']):not([type='hidden']), select, textarea")
            );

            int totalCampos = campos.size();
            System.out.println("Total de campos encontrados: " + totalCampos);

            boolean passou = totalCampos >= 3 && totalCampos <= 10;

            double score;
            if (totalCampos >= 5 && totalCampos <= 7) {
                score = 100.0;
            } else if (totalCampos >= 3 && totalCampos <= 10) {
                score = 80.0;
            } else if (totalCampos > 10) {
                score = Math.max(50.0, 100.0 - (totalCampos - 10) * 5);
            } else {
                score = 60.0;
            }

            boolean saved = saveTestResult("Número Ideal de Campos", "Usabilidade", passou, score,
                    String.format("Formulário possui %d campos (ideal: 5-7)", totalCampos));

            System.out.println("Resultado salvo: " + saved);
            assertTrue("Número de campos fora do ideal", passou);

        } catch (Exception e) {
            System.err.println("Erro no teste testNumeroIdealCampos: " + e.getMessage());
            e.printStackTrace();
            saveTestResult("Número Ideal de Campos", "Usabilidade", false, 0.0,
                    "Erro durante execução: " + e.getMessage());
            fail("Erro durante execução do teste: " + e.getMessage());
        }
    }

    @Test
    public void testMensagensErro() {
        try {
            System.out.println("Executando: testMensagensErro");
            driver.get(formUrl);
            Thread.sleep(1000);

            List<WebElement> errorMessages = driver.findElements(
                    By.cssSelector(".error, .error-message, .invalid-feedback, [class*='error']")
            );

            List<WebElement> campos = driver.findElements(
                    By.cssSelector("input:not([type='submit']):not([type='button']), select, textarea")
            );

            boolean temEstruturaMensagens = !errorMessages.isEmpty() || campos.isEmpty();
            double score = temEstruturaMensagens ? 100.0 : 50.0;

            boolean saved = saveTestResult("Estrutura de Mensagens de Erro", "Usabilidade", temEstruturaMensagens, score,
                    String.format("Encontrados %d containers para mensagens de erro", errorMessages.size()));

            System.out.println("Resultado salvo: " + saved);
            assertTrue("Estrutura de mensagens de erro não encontrada", temEstruturaMensagens);

        } catch (Exception e) {
            System.err.println("Erro no teste testMensagensErro: " + e.getMessage());
            e.printStackTrace();
            saveTestResult("Estrutura de Mensagens de Erro", "Usabilidade", false, 0.0,
                    "Erro durante execução: " + e.getMessage());
            fail("Erro durante execução do teste: " + e.getMessage());
        }
    }

    @Test
    public void testBotaoSubmitVisivel() {
        try {
            System.out.println("Executando: testBotaoSubmitVisivel");
            driver.get(formUrl);
            Thread.sleep(1000);

            List<WebElement> submitButtons = driver.findElements(
                    By.cssSelector("input[type='submit'], button[type='submit']")
            );

            boolean passou = false;
            int botoesVisiveis = 0;

            for (WebElement button : submitButtons) {
                if (button.isDisplayed()) {
                    botoesVisiveis++;
                    passou = true;
                }
            }

            double score = passou ? 100.0 : 0.0;

            boolean saved = saveTestResult("Botão Submit Visível", "Usabilidade", passou, score,
                    String.format("%d botões de submit visíveis encontrados", botoesVisiveis));

            System.out.println("Resultado salvo: " + saved);
            assertTrue("Nenhum botão de submit visível", passou);

        } catch (Exception e) {
            System.err.println("Erro no teste testBotaoSubmitVisivel: " + e.getMessage());
            e.printStackTrace();
            saveTestResult("Botão Submit Visível", "Usabilidade", false, 0.0,
                    "Erro durante execução: " + e.getMessage());
            fail("Erro durante execução do teste: " + e.getMessage());
        }
    }

    @Test
    public void testValidacaoEmail() {
        try {
            System.out.println("Executando: testValidacaoEmail");
            driver.get(formUrl);
            Thread.sleep(1000);

            List<WebElement> emailInputs = driver.findElements(
                    By.cssSelector("input[type='email']")
            );

            int emailsComPattern = 0;
            for (WebElement email : emailInputs) {
                String pattern = email.getAttribute("pattern");
                if (pattern != null && !pattern.isEmpty()) {
                    emailsComPattern++;
                }
            }

            double score = emailInputs.isEmpty() ? 100.0 :
                    (emailsComPattern * 100.0) / emailInputs.size();
            boolean passou = score >= 80 || emailInputs.isEmpty();

            boolean saved = saveTestResult("Validação de Email", "Validação", passou, score,
                    String.format("%d/%d campos email com validação pattern",
                            emailsComPattern, emailInputs.size()));

            System.out.println("Resultado salvo: " + saved);

        } catch (Exception e) {
            System.err.println("Erro no teste testValidacaoEmail: " + e.getMessage());
            e.printStackTrace();
            saveTestResult("Validação de Email", "Validação", false, 0.0,
                    "Erro durante execução: " + e.getMessage());
        }
    }

    @Test
    public void testMascarasEntrada() {
        try {
            System.out.println("Executando: testMascarasEntrada");
            driver.get(formUrl);
            Thread.sleep(1000);

            List<WebElement> camposComMascara = driver.findElements(
                    By.cssSelector("input[placeholder], input[pattern], input[maxlength]")
            );

            List<WebElement> todosCampos = driver.findElements(
                    By.cssSelector("input[type='text'], input[type='tel'], input[type='number']")
            );

            if (todosCampos.isEmpty()) {
                saveTestResult("Máscaras de Entrada", "Usabilidade", true, 100.0,
                        "Nenhum campo que necessite máscara");
                return;
            }

            double score = (camposComMascara.size() * 100.0) / todosCampos.size();
            boolean passou = score >= 60;

            boolean saved = saveTestResult("Máscaras de Entrada", "Usabilidade", passou, score,
                    String.format("%d/%d campos com máscaras ou validação",
                            camposComMascara.size(), todosCampos.size()));

            System.out.println("Resultado salvo: " + saved);

        } catch (Exception e) {
            System.err.println("Erro no teste testMascarasEntrada: " + e.getMessage());
            e.printStackTrace();
            saveTestResult("Máscaras de Entrada", "Usabilidade", false, 0.0,
                    "Erro durante execução: " + e.getMessage());
        }
    }

    @Test
    public void testAgrupamentoLogico() {
        try {
            System.out.println("Executando: testAgrupamentoLogico");
            driver.get(formUrl);
            Thread.sleep(1000);

            List<WebElement> fieldsets = driver.findElements(By.tagName("fieldset"));
            List<WebElement> sections = driver.findElements(
                    By.cssSelector("div[class*='group'], div[class*='section'], .form-group")
            );

            int gruposEncontrados = fieldsets.size() + sections.size();
            boolean passou = gruposEncontrados > 0;
            double score = passou ? Math.min(100.0, gruposEncontrados * 25) : 50.0;

            boolean saved = saveTestResult("Agrupamento Lógico de Campos", "Design", passou, score,
                    String.format("Encontrados %d agrupamentos de campos", gruposEncontrados));

            System.out.println("Resultado salvo: " + saved);

        } catch (Exception e) {
            System.err.println("Erro no teste testAgrupamentoLogico: " + e.getMessage());
            e.printStackTrace();
            saveTestResult("Agrupamento Lógico de Campos", "Design", false, 0.0,
                    "Erro durante execução: " + e.getMessage());
        }
    }

    /**
     * Método auxiliar para salvar resultados
     * VERSÃO MELHORADA com tratamento de erros
     */
    private boolean saveTestResult(String testName, String category, boolean passed,
                                   double score, String details) {
        try {
            long execTime = System.currentTimeMillis() - startTime;

            TestResult result = new TestResult();
            result.setTestName(testName);
            result.setCategory(category);
            result.setPassed(passed);
            result.setScore(score);
            result.setDetails(details);
            result.setFormUrl(formUrl);
            result.setExecutionTime(execTime);
            result.setTimestamp(new Timestamp(System.currentTimeMillis()));

            boolean saved = dao.save(result);

            if (saved) {
                System.out.println("✅ Teste salvo com sucesso: " + testName + " (ID: " + result.getId() + ")");
            } else {
                System.err.println("❌ Falha ao salvar teste: " + testName);
            }

            return saved;

        } catch (Exception e) {
            System.err.println("❌ Erro ao salvar resultado do teste '" + testName + "': " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @After
    public void tearDown() {
        try {
            if (driver != null) {
                SeleniumConfig.quitDriver(driver);
                System.out.println("=== Testes de Usabilidade finalizados ===\n");
            }
        } catch (Exception e) {
            System.err.println("Erro ao finalizar driver: " + e.getMessage());
        }
    }
}