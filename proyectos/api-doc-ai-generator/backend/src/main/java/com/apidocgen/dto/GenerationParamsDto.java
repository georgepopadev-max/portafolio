package com.apidocgen.dto;

public class GenerationParamsDto {
    private double temperature;
    private int maxTokens;
    private String detailLevel;
    private String model;

    public GenerationParamsDto() {
        this.temperature = 0.7;
        this.maxTokens = 2000;
        this.detailLevel = "standard";
    }

    public GenerationParamsDto(double temperature, int maxTokens, String detailLevel, String model) {
        this.temperature = temperature;
        this.maxTokens = maxTokens;
        this.detailLevel = detailLevel;
        this.model = model;
    }

    public double getTemperature() { return temperature; }
    public void setTemperature(double temperature) { this.temperature = temperature; }
    public int getMaxTokens() { return maxTokens; }
    public void setMaxTokens(int maxTokens) { this.maxTokens = maxTokens; }
    public String getDetailLevel() { return detailLevel; }
    public void setDetailLevel(String detailLevel) { this.detailLevel = detailLevel; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
}