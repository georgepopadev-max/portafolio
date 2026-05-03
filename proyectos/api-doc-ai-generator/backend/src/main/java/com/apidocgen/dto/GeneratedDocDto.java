package com.apidocgen.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class GeneratedDocDto {
    private UUID id;
    private UUID projectId;
    private int version;
    private String specYaml;
    private LocalDateTime generatedAt;
    private String modelUsed;
    private String status;
    private GenerationParamsDto params;

    public GeneratedDocDto() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getProjectId() { return projectId; }
    public void setProjectId(UUID projectId) { this.projectId = projectId; }
    public int getVersion() { return version; }
    public void setVersion(int version) { this.version = version; }
    public String getSpecYaml() { return specYaml; }
    public void setSpecYaml(String specYaml) { this.specYaml = specYaml; }
    public LocalDateTime getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }
    public String getModelUsed() { return modelUsed; }
    public void setModelUsed(String modelUsed) { this.modelUsed = modelUsed; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public GenerationParamsDto getParams() { return params; }
    public void setParams(GenerationParamsDto params) { this.params = params; }
}