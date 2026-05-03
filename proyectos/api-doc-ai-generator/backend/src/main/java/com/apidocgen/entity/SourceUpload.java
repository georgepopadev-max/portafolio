package com.apidocgen.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "source_uploads")
public class SourceUpload {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;
    
    @Column(nullable = false)
    private String filename;
    
    @Column(name = "file_path")
    private String filePath;
    
    @Column(name = "file_size")
    private long fileSize;
    
    @Column(name = "uploaded_at")
    private LocalDateTime uploadedAt = LocalDateTime.now();
    
    @Column(name = "controller_count")
    private int controllerCount;
    
    @Column(name = "endpoint_count")
    private int endpointCount;
    
    @Column(columnDefinition = "TEXT")
    private String sourceCode;

    public SourceUpload() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public Project getProject() { return project; }
    public void setProject(Project project) { this.project = project; }
    public String getFilename() { return filename; }
    public void setFilename(String filename) { this.filename = filename; }
    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
    public long getFileSize() { return fileSize; }
    public void setFileSize(long fileSize) { this.fileSize = fileSize; }
    public LocalDateTime getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }
    public int getControllerCount() { return controllerCount; }
    public void setControllerCount(int controllerCount) { this.controllerCount = controllerCount; }
    public int getEndpointCount() { return endpointCount; }
    public void setEndpointCount(int endpointCount) { this.endpointCount = endpointCount; }
    public String getSourceCode() { return sourceCode; }
    public void setSourceCode(String sourceCode) { this.sourceCode = sourceCode; }
}