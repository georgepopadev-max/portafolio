package com.incident.assistant.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "resolutions")
public class Resolution {
    
    @Id
    private String id;
    
    @Column(nullable = false)
    private String threadId;
    
    @Column(columnDefinition = "TEXT")
    private String summary;
    
    @Column(columnDefinition = "TEXT")
    private String steps;
    
    private String jiraTicketId;
    
    private String resolvedBy;
    
    @Column(nullable = false)
    private LocalDateTime resolvedAt;
    
    public Resolution() {
        this.id = UUID.randomUUID().toString();
        this.resolvedAt = LocalDateTime.now();
    }
    
    public Resolution(String threadId, String summary, String steps) {
        this();
        this.threadId = threadId;
        this.summary = summary;
        this.steps = steps;
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getThreadId() { return threadId; }
    public void setThreadId(String threadId) { this.threadId = threadId; }
    
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    
    public String getSteps() { return steps; }
    public void setSteps(String steps) { this.steps = steps; }
    
    public String getJiraTicketId() { return jiraTicketId; }
    public void setJiraTicketId(String jiraTicketId) { this.jiraTicketId = jiraTicketId; }
    
    public String getResolvedBy() { return resolvedBy; }
    public void setResolvedBy(String resolvedBy) { this.resolvedBy = resolvedBy; }
    
    public LocalDateTime getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; }
}
