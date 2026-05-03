package com.incident.assistant.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "documents")
public class Document {
    
    @Id
    private String id;
    
    @Column(nullable = false)
    private String sourceType;
    
    private String sourceId;
    
    @Column(nullable = false)
    private String title;
    
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;
    
    @Column(columnDefinition = "TEXT")
    private String metadata;
    
    @Column(nullable = false)
    private LocalDateTime indexedAt;
    
    public Document() {
        this.id = UUID.randomUUID().toString();
        this.indexedAt = LocalDateTime.now();
    }
    
    public Document(String sourceType, String sourceId, String title, String content) {
        this();
        this.sourceType = sourceType;
        this.sourceId = sourceId;
        this.title = title;
        this.content = content;
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getSourceType() { return sourceType; }
    public void setSourceType(String sourceType) { this.sourceType = sourceType; }
    
    public String getSourceId() { return sourceId; }
    public void setSourceId(String sourceId) { this.sourceId = sourceId; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public String getMetadata() { return metadata; }
    public void setMetadata(String metadata) { this.metadata = metadata; }
    
    public LocalDateTime getIndexedAt() { return indexedAt; }
    public void setIndexedAt(LocalDateTime indexedAt) { this.indexedAt = indexedAt; }
}
