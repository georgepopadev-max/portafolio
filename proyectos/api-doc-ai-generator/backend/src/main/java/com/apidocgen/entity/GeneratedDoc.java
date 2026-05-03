package com.apidocgen.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "generated_docs")
public class GeneratedDoc {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;
    
    private int version;
    
    @Column(name = "spec_yaml", columnDefinition = "TEXT")
    private String specYaml;
    
    @Column(name = "spec_json", columnDefinition = "TEXT")
    private String specJson;
    
    @Column(name = "generated_at")
    private LocalDateTime generatedAt = LocalDateTime.now();
    
    @Column(name = "model_used")
    private String modelUsed;
    
    private String status = "COMPLETED";
    
    public GeneratedDoc() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public Project getProject() { return project; }
    public void setProject(Project project) { this.project = project; }
    public int getVersion() { return version; }
    public void setVersion(int version) { this.version = version; }
    public String getSpecYaml() { return specYaml; }
    public void setSpecYaml(String specYaml) { this.specYaml = specYaml; }
    public String getSpecJson() { return specJson; }
    public void setSpecJson(String specJson) { this.specJson = specJson; }
    public LocalDateTime getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }
    public String getModelUsed() { return modelUsed; }
    public void setModelUsed(String modelUsed) { this.modelUsed = modelUsed; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}