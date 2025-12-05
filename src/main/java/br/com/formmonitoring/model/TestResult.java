package br.com.formmonitoring.model;

import java.sql.Timestamp;

public class TestResult {

    private int id;
    private String testName;
    private String category;
    private boolean passed;
    private double score;
    private String details;
    private String formUrl;
    private long executionTime;
    private Timestamp timestamp;

    public TestResult() {}

    public TestResult(String testName, String category, boolean passed, double score, String details) {
        this.testName = testName;
        this.category = category;
        this.passed = passed;
        this.score = score;
        this.details = details;
        this.timestamp = new Timestamp(System.currentTimeMillis());
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isPassed() {
        return passed;
    }

    public void setPassed(boolean passed) {
        this.passed = passed;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getFormUrl() {
        return formUrl;
    }

    public void setFormUrl(String formUrl) {
        this.formUrl = formUrl;
    }

    public long getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(long executionTime) {
        this.executionTime = executionTime;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "TestResult{" +
                "id=" + id +
                ", testName='" + testName + '\'' +
                ", category='" + category + '\'' +
                ", passed=" + passed +
                ", score=" + score +
                ", details='" + details + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}