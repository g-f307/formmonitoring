package br.com.formmonitoring.tests;

import br.com.formmonitoring.config.SeleniumConfig;
import br.com.formmonitoring.model.TestResult;
import br.com.formmonitoring.validators.PerformanceValidator;
import org.junit.jupiter.api.*;
import org.openqa.selenium.WebDriver;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Testes de Performance e Responsividade")
class FormPerformanceTest {

    private static final String FORM_URL = "http://localhost:8080/form-monitoring/jsp/form-exemplo.jsp";
    private static final long MAX_LOAD_TIME_MS = 5000L;
    private static final double MIN_FONT_SIZE_PX = 12.0;
    private static final double MIN_SCORE_THRESHOLD = 60.0;

    private WebDriver driver;
    private PerformanceValidator validator;

    @BeforeEach
    void setUp() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("🚀 Inicializando teste de Performance");
        System.out.println("=".repeat(60));

        driver = SeleniumConfig.getDriver();
        validator = new PerformanceValidator();

        assertNotNull(driver, "WebDriver não foi inicializado corretamente");
    }

    @Test
    @Order(1)
    @DisplayName("Deve validar que a página carrega em menos de 5 segundos")
    void deveCarregarPaginaRapidamente() {
        System.out.println("📋 Teste: Tempo de carregamento");

        long startTime = System.currentTimeMillis();
        TestResult result = validator.validateLoadingTime(driver, FORM_URL);
        long actualLoadTime = result.getExecutionTime();

        assertNotNull(result, "Resultado do teste não pode ser nulo");

        System.out.println("   ✓ Categoria: " + result.getCategory());
        System.out.println("   ✓ Score: " + String.format("%.1f%%", result.getScore()));
        System.out.println("   ✓ Tempo de carga: " + actualLoadTime + "ms");
        System.out.println("   ✓ Detalhes: " + result.getDetails());
        System.out.println("   ✓ Status: " + (result.isPassed() ? "✅ PASSOU" : "❌ FALHOU"));

        assertTrue(result.isPassed(),
                String.format("Página levou %dms para carregar (limite: %dms)",
                        actualLoadTime, MAX_LOAD_TIME_MS));

        assertTrue(actualLoadTime <= MAX_LOAD_TIME_MS,
                String.format("Tempo de carregamento (%dms) excede o limite de %dms",
                        actualLoadTime, MAX_LOAD_TIME_MS));

        assertEquals("Performance", result.getCategory(),
                "Categoria do teste deve ser Performance");
    }

    @Test
    @Order(2)
    @DisplayName("Deve validar responsividade em dispositivos móveis (375x667)")
    void deveSerResponsivoEmMobile() {
        System.out.println("📋 Teste: Responsividade mobile (375x667)");

        TestResult result = validator.validateMobileResponsiveness(driver, FORM_URL);

        assertNotNull(result, "Resultado do teste não pode ser nulo");

        System.out.println("   ✓ Categoria: " + result.getCategory());
        System.out.println("   ✓ Score: " + String.format("%.1f%%", result.getScore()));
        System.out.println("   ✓ Detalhes: " + result.getDetails());
        System.out.println("   ✓ Status: " + (result.isPassed() ? "✅ PASSOU" : "❌ FALHOU"));

        assertTrue(result.isPassed(),
                "Formulário não está responsivo em mobile: " + result.getDetails());

        assertTrue(result.getScore() >= 90.0,
                "Score de responsividade mobile está abaixo do esperado");

        // Validação de categoria
        assertEquals("Design", result.getCategory(),
                "Categoria do teste deve ser Design");
    }

    @Test
    @Order(3)
    @DisplayName("Deve validar responsividade em tablets (768x1024)")
    void deveSerResponsivoEmTablet() {
        System.out.println("📋 Teste: Responsividade tablet (768x1024)");

        TestResult result = validator.validateTabletResponsiveness(driver, FORM_URL);

        assertNotNull(result, "Resultado do teste não pode ser nulo");

        System.out.println("   ✓ Categoria: " + result.getCategory());
        System.out.println("   ✓ Score: " + String.format("%.1f%%", result.getScore()));
        System.out.println("   ✓ Detalhes: " + result.getDetails());
        System.out.println("   ✓ Status: " + (result.isPassed() ? "✅ PASSOU" : "❌ FALHOU"));

        assertTrue(result.isPassed(),
                "Formulário não está responsivo em tablet: " + result.getDetails());

        assertTrue(result.getScore() >= 90.0,
                "Score de responsividade tablet está abaixo do esperado");

        assertEquals("Design", result.getCategory(),
                "Categoria do teste deve ser Design");
    }

    @Test
    @Order(4)
    @DisplayName("Deve validar que fontes têm tamanho mínimo de 12px")
    void deveValidarTamanhoDeFonte() {
        System.out.println("📋 Teste: Tamanho de fonte (mínimo 12px)");

        TestResult result = validator.validateFontSize(driver, FORM_URL);

        assertNotNull(result, "Resultado do teste não pode ser nulo");

        System.out.println("   ✓ Categoria: " + result.getCategory());
        System.out.println("   ✓ Score: " + String.format("%.1f%%", result.getScore()));
        System.out.println("   ✓ Detalhes: " + result.getDetails());
        System.out.println("   ✓ Status: " + (result.isPassed() ? "✅ PASSOU" : "❌ FALHOU"));

        assertTrue(result.getScore() >= 80.0,
                String.format("Score de tamanho de fonte (%.1f%%) está abaixo de 80%%",
                        result.getScore()));

        assertTrue(result.isPassed(),
                "Fontes muito pequenas detectadas (< " + MIN_FONT_SIZE_PX + "px): " +
                        result.getDetails());

        assertEquals("Design", result.getCategory(),
                "Categoria do teste deve ser Design");
    }

    @Test
    @Order(5)
    @DisplayName("Deve validar espaçamento adequado entre campos do formulário")
    void deveValidarEspacamentoEntreCampos() {
        System.out.println("📋 Teste: Espaçamento entre campos");

        TestResult result = validator.validateFieldSpacing(driver, FORM_URL);

        assertNotNull(result, "Resultado do teste não pode ser nulo");

        System.out.println("   ✓ Categoria: " + result.getCategory());
        System.out.println("   ✓ Score: " + String.format("%.1f%%", result.getScore()));
        System.out.println("   ✓ Detalhes: " + result.getDetails());
        System.out.println("   ✓ Status: " + (result.isPassed() ? "✅ PASSOU" : "❌ FALHOU"));

        assertTrue(result.getScore() >= MIN_SCORE_THRESHOLD,
                String.format("Score de espaçamento (%.1f%%) está abaixo do mínimo (%.1f%%)",
                        result.getScore(), MIN_SCORE_THRESHOLD));

        assertTrue(result.isPassed(),
                "Espaçamento inadequado entre campos: " + result.getDetails());

        assertEquals("Design", result.getCategory(),
                "Categoria do teste deve ser Design");
    }

    @Test
    @Order(6)
    @Tag("slow")
    @DisplayName("Deve validar performance geral do formulário em múltiplos carregamentos")
    void deveValidarPerformanceGeralEmMultiplosCarregamentos() {
        System.out.println("📋 Teste: Performance em múltiplos carregamentos");

        final int NUM_ITERATIONS = 3;
        long totalLoadTime = 0;

        for (int i = 1; i <= NUM_ITERATIONS; i++) {
            System.out.println("   🔄 Iteração " + i + "/" + NUM_ITERATIONS);
            TestResult result = validator.validateLoadingTime(driver, FORM_URL);
            totalLoadTime += result.getExecutionTime();

            assertTrue(result.isPassed(),
                    "Falha na iteração " + i + ": " + result.getDetails());
        }

        long avgLoadTime = totalLoadTime / NUM_ITERATIONS;

        System.out.println("\n   📊 Estatísticas:");
        System.out.println("   ✓ Total de carregamentos: " + NUM_ITERATIONS);
        System.out.println("   ✓ Tempo total: " + totalLoadTime + "ms");
        System.out.println("   ✓ Tempo médio: " + avgLoadTime + "ms");
        System.out.println("   ✓ Limite: " + MAX_LOAD_TIME_MS + "ms");
        System.out.println("   ✓ Status: " + (avgLoadTime <= MAX_LOAD_TIME_MS ? "✅ PASSOU" : "❌ FALHOU"));

        assertTrue(avgLoadTime <= MAX_LOAD_TIME_MS,
                String.format("Tempo médio de carregamento (%dms) excede o limite de %dms",
                        avgLoadTime, MAX_LOAD_TIME_MS));
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
        System.out.println("📊 RESUMO: Testes de Performance concluídos");
        System.out.println("   • Total de validações: 6");
        System.out.println("   • Foco: Performance, responsividade e design");
        System.out.println("   • Limite de carregamento: " + MAX_LOAD_TIME_MS + "ms");
        System.out.println("=".repeat(60) + "\n");
    }
}