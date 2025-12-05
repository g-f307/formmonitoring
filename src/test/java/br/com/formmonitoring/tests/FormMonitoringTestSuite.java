package br.com.formmonitoring.tests;

import org.junit.platform.suite.api.*;

@Suite
@SuiteDisplayName("🚀 Form Monitoring - Suite Completa de Testes")
@SelectClasses({
        FormAccessibilityTest.class,
        FormUsabilityTest.class,
        FormPerformanceTest.class
})
@IncludeTags({"accessibility", "usability", "performance"})
public class FormMonitoringTestSuite {
}