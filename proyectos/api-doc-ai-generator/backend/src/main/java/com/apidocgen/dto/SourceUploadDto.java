package com.apidocgen.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class SourceUploadDto {
    private UUID id;
    private String filename;
    private long fileSize;
    private LocalDateTime uploadedAt;
    private int controllerCount;
    private int endpointCount;

    public SourceUploadDto() {}

    public SourceUploadDto(UUID id, String filename, long fileSize, 
                          LocalDateTime uploadedAt, int controllerCount, int endpointCount) {
        this.id = id;
        this.filename = filename;
        this.fileSize = fileSize;
        this.uploadedAt = uploadedAt;
        this.controllerCount = controllerCount;
        this.endpointCount = endpointCount;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getFilename() { return filename; }
    public void setFilename(String filename) { this.filename = filename; }
    public long getFileSize() { return fileSize; }
    public void setFileSize(long fileSize) { this.fileSize = fileSize; }
    public LocalDateTime getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }
    public int getControllerCount() { return controllerCount; }
    public void setControllerCount(int controllerCount) { this.controllerCount = controllerCount; }
    public int getEndpointCount() { return endpointCount; }
    public void setEndpointCount(int endpointCount) { this.endpointCount = endpointCount; }
}