package br.com.formmonitoring.tests;

import br.com.formmonitoring.config.SeleniumConfig;
import br.com.formmonitoring.model.TestResult;
import br.com.formmonitoring.validators.AccessibilityValidator;
import org.junit.jupiter.api.*;
import org.openqa.selenium.WebDriver;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Testes de Acessibilidade Web")
class FormAccessibilityTest {

    private static final String FORM_URL = "http://localhost:8080/form-monitoring/jsp/form-exemplo.jsp";
    private static final double MIN_SCORE_THRESHOLD = 70.0;

    private WebDriver driver;
    private AccessibilityValidator validator;

    @BeforeEach
    void setUp() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("🚀 Inicializando teste de Acessibilidade");
        System.out.println("=".repeat(60));

        driver = SeleniumConfig.getDriver();
        validator = new AccessibilityValidator();

        assertNotNull(driver, "WebDriver não foi inicializado corretamente");
    }

    @Test
    @Order(1)
    @DisplayName("Deve validar que todos os inputs possuem labels associados")
    void deveValidarLabelsAssociados() {
        System.out.println("📋 Teste: Labels associados aos campos");

        TestResult result = validator.validateAssociatedLabels(driver, FORM_URL);

        assertNotNull(result, "Resultado do teste não pode ser nulo");

        System.out.println("   ✓ Categoria: " + result.getCategory());
        System.out.println("   ✓ Score: " + String.format("%.1f%%", result.getScore()));
        System.out.println("   ✓ Detalhes: " + result.getDetails());
        System.out.println("   ✓ Status: " + (result.isPassed() ? "✅ PASSOU" : "❌ FALHOU"));

        assertTrue(result.getScore() >= MIN_SCORE_THRESHOLD,
                String.format("Score de labels (%.1f%%) está abaixo do mínimo aceitável (%.1f%%)",
                        result.getScore(), MIN_SCORE_THRESHOLD));

        assertTrue(result.isPassed(),
                "Teste de labels associados falhou: " + result.getDetails());
    }

    @Test
    @Order(2)
    @DisplayName("Deve validar indicação visual de campos obrigatórios")
    void deveValidarIndicacaoCamposObrigatorios() {
        System.out.println("📋 Teste: Indicação de campos obrigatórios");

        TestResult result = validator.validateRequiredFieldsIndication(driver, FORM_URL);

        assertNotNull(result, "Resultado do teste não pode ser nulo");

        System.out.println("   ✓ Categoria: " + result.getCategory());
        System.out.println("   ✓ Score: " + String.format("%.1f%%", result.getScore()));
        System.out.println("   ✓ Detalhes: " + result.getDetails());
        System.out.println("   ✓ Status: " + (result.isPassed() ? "✅ PASSOU" : "❌ FALHOU"));

        assertTrue(result.getScore() >= 60.0,
                String.format("Score de indicação de campos obrigatórios (%.1f%%) está abaixo do mínimo (60%%)",
                        result.getScore()));

        assertTrue(result.isPassed(),
                "Campos obrigatórios sem indicação visual adequada: " + result.getDetails());
    }

    @Test
    @Order(3)
    @DisplayName("Deve validar presença de atributos ARIA para acessibilidade")
    void deveValidarAtributosARIA() {
        System.out.println("📋 Teste: Atributos ARIA");

        TestResult result = validator.validateARIAAttributes(driver, FORM_URL);

        assertNotNull(result, "Resultado do teste não pode ser nulo");

        System.out.println("   ✓ Categoria: " + result.getCategory());
        System.out.println("   ✓ Score: " + String.format("%.1f%%", result.getScore()));
        System.out.println("   ✓ Detalhes: " + result.getDetails());
        System.out.println("   ✓ Status: " + (result.isPassed() ? "✅ PASSOU" : "❌ FALHOU"));

        assertTrue(result.getScore() >= 30.0,
                String.format("Score de atributos ARIA (%.1f%%) está abaixo do mínimo (30%%)",
                        result.getScore()));

        assertTrue(result.isPassed(),
                "Poucos elementos com atributos ARIA: " + result.getDetails());
    }

    @Test
    @Order(4)
    @DisplayName("Deve validar visibilidade e contraste de elementos do formulário")
    void deveValidarVisibilidadeElementos() {
        System.out.println("📋 Teste: Visibilidade de elementos");

        TestResult result = validator.validateElementVisibility(driver, FORM_URL);

        assertNotNull(result, "Resultado do teste não pode ser nulo");

        System.out.println("   ✓ Categoria: " + result.getCategory());
        System.out.println("   ✓ Score: " + String.format("%.1f%%", result.getScore()));
        System.out.println("   ✓ Detalhes: " + result.getDetails());
        System.out.println("   ✓ Status: " + (result.isPassed() ? "✅ PASSOU" : "❌ FALHOU"));

        assertTrue(result.getScore() >= 90.0,
                String.format("Score de visibilidade (%.1f%%) está abaixo do mínimo (90%%)",
                        result.getScore()));

        assertTrue(result.isPassed(),
                "Elementos com baixa visibilidade detectados: " + result.getDetails());

        // Validação adicional
        assertEquals("Acessibilidade", result.getCategory(),
                "Categoria do teste deve ser Acessibilidade");
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
        System.out.println("📊 RESUMO: Testes de Acessibilidade concluídos");
        System.out.println("=".repeat(60) + "\n");
    }
}