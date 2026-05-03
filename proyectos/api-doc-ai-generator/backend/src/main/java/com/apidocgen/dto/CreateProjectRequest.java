package com.apidocgen.dto;

public class CreateProjectRequest {
    private String name;
    private String description;
    private String aiProvider;

    public CreateProjectRequest() {}

    public CreateProjectRequest(String name, String description, String aiProvider) {
        this.name = name;
        this.description = description;
        this.aiProvider = aiProvider;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getAiProvider() { return aiProvider; }
    public void setAiProvider(String aiProvider) { this.aiProvider = aiProvider; }
}