package br.com.formmonitoring.tests;

import br.com.formmonitoring.config.SeleniumConfig;
import br.com.formmonitoring.model.TestResult;
import br.com.formmonitoring.validators.UsabilityValidator;
import org.junit.jupiter.api.*;
import org.openqa.selenium.WebDriver;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Testes de Usabilidade Web")
class FormUsabilityTest {

    private static final String FORM_URL = "http://localhost:8080/form-monitoring/jsp/form-exemplo.jsp";
    private static final int MIN_FIELDS = 3;
    private static final int MAX_FIELDS = 10;
    private static final double MIN_SCORE_THRESHOLD = 60.0;

    private WebDriver driver;
    private UsabilityValidator validator;

    @BeforeEach
    void setUp() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("🚀 Inicializando teste de Usabilidade");
        System.out.println("=".repeat(60));

        driver = SeleniumConfig.getDriver();
        validator = new UsabilityValidator();

        assertNotNull(driver, "WebDriver não foi inicializado corretamente");
    }

    @Test
    @Order(1)
    @DisplayName("Deve validar que o formulário tem número ideal de campos (3-10)")
    void deveValidarNumeroIdealDeCampos() {
        System.out.println("📋 Teste: Número ideal de campos");

        TestResult result = validator.validateFieldCount(driver, FORM_URL);

        assertNotNull(result, "Resultado do teste não pode ser nulo");

        System.out.println("   ✓ Categoria: " + result.getCategory());
        System.out.println("   ✓ Score: " + String.format("%.1f%%", result.getScore()));
        System.out.println("   ✓ Detalhes: " + result.getDetails());
        System.out.println("   ✓ Status: " + (result.isPassed() ? "✅ PASSOU" : "❌ FALHOU"));

        assertTrue(result.isPassed(),
                String.format("Número de campos inadequado. Ideal: %d-%d campos. %s",
                        MIN_FIELDS, MAX_FIELDS, result.getDetails()));

        assertTrue(result.getScore() >= 60.0,
                "Score muito baixo para contagem de campos");
    }

    @Test
    @Order(2)
    @DisplayName("Deve validar presença e visibilidade do botão de submit")
    void deveValidarBotaoSubmitVisivel() {
        System.out.println("📋 Teste: Botão submit visível");

        TestResult result = validator.validateSubmitButton(driver, FORM_URL);

        assertNotNull(result, "Resultado do teste não pode ser nulo");

        System.out.println("   ✓ Categoria: " + result.getCategory());
        System.out.println("   ✓ Score: " + String.format("%.1f%%", result.getScore()));
        System.out.println("   ✓ Detalhes: " + result.getDetails());
        System.out.println("   ✓ Status: " + (result.isPassed() ? "✅ PASSOU" : "❌ FALHOU"));

        assertTrue(result.isPassed(),
                "Botão submit não encontrado ou não visível: " + result.getDetails());

        assertTrue(result.getScore() >= 80.0,
                "Botão submit encontrado mas com problemas de usabilidade");
    }

    @Test
    @Order(3)
    @DisplayName("Deve validar estrutura de mensagens de erro para feedback ao usuário")
    void deveValidarEstruturaDeMensagensDeErro() {
        System.out.println("📋 Teste: Estrutura de mensagens de erro");

        TestResult result = validator.validateErrorMessages(driver, FORM_URL);

        assertNotNull(result, "Resultado do teste não pode ser nulo");

        System.out.println("   ✓ Categoria: " + result.getCategory());
        System.out.println("   ✓ Score: " + String.format("%.1f%%", result.getScore()));
        System.out.println("   ✓ Detalhes: " + result.getDetails());
        System.out.println("   ✓ Status: " + (result.isPassed() ? "✅ PASSOU" : "❌ FALHOU"));

        assertTrue(result.isPassed(),
                "Estrutura de mensagens de erro inadequada: " + result.getDetails());

        assertEquals("Usabilidade", result.getCategory(),
                "Categoria do teste deve ser Usabilidade");
    }

    @Test
    @Order(4)
    @DisplayName("Deve validar presença de máscaras de entrada para campos específicos")
    void deveValidarMascarasDeEntrada() {
        System.out.println("📋 Teste: Máscaras de entrada");

        TestResult result = validator.validateInputMasks(driver, FORM_URL);

        assertNotNull(result, "Resultado do teste não pode ser nulo");

        System.out.println("   ✓ Categoria: " + result.getCategory());
        System.out.println("   ✓ Score: " + String.format("%.1f%%", result.getScore()));
        System.out.println("   ✓ Detalhes: " + result.getDetails());
        System.out.println("   ✓ Status: " + (result.isPassed() ? "✅ PASSOU" : "❌ FALHOU"));

        assertTrue(result.getScore() >= 50.0,
                "Poucos campos com máscaras ou validação de entrada");

        assertTrue(result.isPassed(),
                "Máscaras de entrada inadequadas: " + result.getDetails());
    }

    @Test
    @Order(5)
    @DisplayName("Deve validar uso correto de type='email' para campos de e-mail")
    void deveValidarValidacaoDeEmail() {
        System.out.println("📋 Teste: Validação de email");

        TestResult result = validator.validateEmailValidation(driver, FORM_URL);

        assertNotNull(result, "Resultado do teste não pode ser nulo");

        System.out.println("   ✓ Categoria: " + result.getCategory());
        System.out.println("   ✓ Score: " + String.format("%.1f%%", result.getScore()));
        System.out.println("   ✓ Detalhes: " + result.getDetails());
        System.out.println("   ✓ Status: " + (result.isPassed() ? "✅ PASSOU" : "❌ FALHOU"));

        assertTrue(result.isPassed(),
                "Campos de email com type incorreto: " + result.getDetails());

        assertEquals("Validação", result.getCategory(),
                "Categoria do teste deve ser Validação");
    }

    @Test
    @Order(6)
    @DisplayName("Deve validar agrupamento lógico de campos relacionados")
    void deveValidarAgrupamentoLogicoDeCampos() {
        System.out.println("📋 Teste: Agrupamento lógico de campos");

        TestResult result = validator.validateFieldGrouping(driver, FORM_URL);

        assertNotNull(result, "Resultado do teste não pode ser nulo");

        System.out.println("   ✓ Categoria: " + result.getCategory());
        System.out.println("   ✓ Score: " + String.format("%.1f%%", result.getScore()));
        System.out.println("   ✓ Detalhes: " + result.getDetails());
        System.out.println("   ✓ Status: " + (result.isPassed() ? "✅ PASSOU" : "❌ FALHOU"));

        assertTrue(result.isPassed(),
                "Agrupamento lógico inadequado: " + result.getDetails());

        assertTrue(result.getScore() >= MIN_SCORE_THRESHOLD,
                String.format("Score de agrupamento (%.1f%%) está abaixo do mínimo (%.1f%%)",
                        result.getScore(), MIN_SCORE_THRESHOLD));

        assertEquals("Design", result.getCategory(),
                "Categoria do teste deve ser Design");
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            SeleniumConfig.quitDriver(driver);
            System.out.println("\n🏁 Teste finalizado - WebDriver encerrado");
            System.out.println("=".repeat(60) + "\n");
        }
    }

    @AfterAll
    static void summary() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("📊 RESUMO: Testes de Usabilidade concluídos");
        System.out.println("   • Total de validações: 6");
        System.out.println("   • Foco: UX/UI e experiência do usuário");
        System.out.println("=".repeat(60) + "\n");
    }
}