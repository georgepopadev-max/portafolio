package com.apidocgen.dto;

import java.util.List;
import java.util.UUID;

public class ProjectDto {
    private UUID id;
    private String name;
    private String description;
    private int endpointCount;
    private String status;
    private String lastGenerated;
    private String aiProvider;
    private List<SourceUploadDto> uploads;

    public ProjectDto() {}

    public ProjectDto(UUID id, String name, String description, int endpointCount, 
                      String status, String lastGenerated, String aiProvider) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.endpointCount = endpointCount;
        this.status = status;
        this.lastGenerated = lastGenerated;
        this.aiProvider = aiProvider;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public int getEndpointCount() { return endpointCount; }
    public void setEndpointCount(int endpointCount) { this.endpointCount = endpointCount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getLastGenerated() { return lastGenerated; }
    public void setLastGenerated(String lastGenerated) { this.lastGenerated = lastGenerated; }
    public String getAiProvider() { return aiProvider; }
    public void setAiProvider(String aiProvider) { this.aiProvider = aiProvider; }
    public List<SourceUploadDto> getUploads() { return uploads; }
    public void setUploads(List<SourceUploadDto> uploads) { this.uploads = uploads; }
}