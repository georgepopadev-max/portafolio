package com.incident.assistant.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "document_chunks")
public class DocumentChunk {
    
    @Id
    private String id;
    
    @Column(nullable = false)
    private String documentId;
    
    @Column(columnDefinition = "TEXT", nullable = false)
    private String chunkText;
    
    private int position;
    
    private double relevanceScore;
    
    public DocumentChunk() {
        this.id = UUID.randomUUID().toString();
    }
    
    public DocumentChunk(String documentId, String chunkText, int position) {
        this();
        this.documentId = documentId;
        this.chunkText = chunkText;
        this.position = position;
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getDocumentId() { return documentId; }
    public void setDocumentId(String documentId) { this.documentId = documentId; }
    
    public String getChunkText() { return chunkText; }
    public void setChunkText(String chunkText) { this.chunkText = chunkText; }
    
    public int getPosition() { return position; }
    public void setPosition(int position) { this.position = position; }
    
    public double getRelevanceScore() { return relevanceScore; }
    public void setRelevanceScore(double relevanceScore) { this.relevanceScore = relevanceScore; }
}
