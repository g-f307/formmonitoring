package br.com.formmonitoring.dao;

import br.com.formmonitoring.model.TestResult;
import br.com.formmonitoring.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestResultDAO {

    public boolean save(TestResult result) {
        String sql = "INSERT INTO test_results (test_name, category, passed, score, details, form_url, execution_time, timestamp) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, result.getTestName());
            stmt.setString(2, result.getCategory());
            stmt.setBoolean(3, result.isPassed());
            stmt.setDouble(4, result.getScore());
            stmt.setString(5, result.getDetails());
            stmt.setString(6, result.getFormUrl());
            stmt.setLong(7, result.getExecutionTime());
            stmt.setTimestamp(8, result.getTimestamp());

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    result.setId(rs.getInt(1));
                }
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Erro ao salvar resultado: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    public List<TestResult> getRecentResults(int limit) {
        String sql = "SELECT * FROM test_results ORDER BY timestamp DESC LIMIT ?";
        List<TestResult> results = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, limit);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                results.add(extractTestResult(rs));
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar resultados: " + e.getMessage());
        }

        return results;
    }

    public Map<String, CategoryStats> getCategoryStatistics() {
        String sql = "SELECT category, " +
                "COUNT(*) as total, " +
                "SUM(CASE WHEN passed = 1 THEN 1 ELSE 0 END) as passed, " +
                "AVG(score) as avg_score " +
                "FROM test_results " +
                "GROUP BY category";

        Map<String, CategoryStats> stats = new HashMap<>();

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                CategoryStats cs = new CategoryStats();
                cs.category = rs.getString("category");
                cs.total = rs.getInt("total");
                cs.passed = rs.getInt("passed");
                cs.avgScore = rs.getDouble("avg_score");

                stats.put(cs.category, cs);
            }

        } catch (SQLException e) {
            System.err.println("Erro ao calcular estatísticas: " + e.getMessage());
        }

        return stats;
    }

    public double getAverageScore() {
        String sql = "SELECT AVG(score) as avg_score FROM test_results";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getDouble("avg_score");
            }

        } catch (SQLException e) {
            System.err.println("Erro ao calcular score médio: " + e.getMessage());
        }

        return 0.0;
    }

    public List<TestResult> getResultsByCategory(String category) {
        String sql = "SELECT * FROM test_results WHERE category = ? ORDER BY timestamp DESC";
        List<TestResult> results = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, category);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                results.add(extractTestResult(rs));
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar por categoria: " + e.getMessage());
        }

        return results;
    }

    public int countPassedTests() {
        String sql = "SELECT COUNT(*) as total FROM test_results WHERE passed = 1";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt("total");
            }

        } catch (SQLException e) {
            System.err.println("Erro ao contar testes passados: " + e.getMessage());
        }

        return 0;
    }

    public int countFailedTests() {
        String sql = "SELECT COUNT(*) as total FROM test_results WHERE passed = 0";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt("total");
            }

        } catch (SQLException e) {
            System.err.println("Erro ao contar testes falhados: " + e.getMessage());
        }

        return 0;
    }

    private TestResult extractTestResult(ResultSet rs) throws SQLException {
        TestResult result = new TestResult();
        result.setId(rs.getInt("id"));
        result.setTestName(rs.getString("test_name"));
        result.setCategory(rs.getString("category"));
        result.setPassed(rs.getBoolean("passed"));
        result.setScore(rs.getDouble("score"));
        result.setDetails(rs.getString("details"));
        result.setFormUrl(rs.getString("form_url"));
        result.setExecutionTime(rs.getLong("execution_time"));
        result.setTimestamp(rs.getTimestamp("timestamp"));
        return result;
    }

    public static class CategoryStats {
        private String category;
        private int total;
        private int passed;
        private double avgScore;

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public int getPassed() {
            return passed;
        }

        public void setPassed(int passed) {
            this.passed = passed;
        }

        public double getAvgScore() {
            return avgScore;
        }

        public void setAvgScore(double avgScore) {
            this.avgScore = avgScore;
        }
    }
}