package com.incident.assistant.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "incident_threads")
public class IncidentThread {
    
    @Id
    private String id;
    
    @Column(nullable = false)
    private String title;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ThreadStatus status;
    
    private String category;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    private LocalDateTime resolvedAt;
    
    private String outcome;
    
    public IncidentThread() {
        this.id = UUID.randomUUID().toString();
        this.status = ThreadStatus.ACTIVE;
        this.createdAt = LocalDateTime.now();
    }
    
    public IncidentThread(String title) {
        this();
        this.title = title;
    }
    
    public enum ThreadStatus {
        ACTIVE, RESOLVED, ESCALATED
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public ThreadStatus getStatus() { return status; }
    public void setStatus(ThreadStatus status) { this.status = status; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; }
    
    public String getOutcome() { return outcome; }
    public void setOutcome(String outcome) { this.outcome = outcome; }
}
