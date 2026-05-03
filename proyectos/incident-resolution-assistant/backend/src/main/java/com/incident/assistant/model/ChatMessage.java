package com.incident.assistant.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "chat_messages")
public class ChatMessage {
    
    @Id
    private String id;
    
    @Column(nullable = false)
    private String threadId;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageRole role;
    
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;
    
    private String citations;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    public ChatMessage() {
        this.id = UUID.randomUUID().toString();
        this.createdAt = LocalDateTime.now();
    }
    
    public ChatMessage(String threadId, MessageRole role, String content) {
        this();
        this.threadId = threadId;
        this.role = role;
        this.content = content;
    }
    
    public enum MessageRole {
        USER, ASSISTANT, SYSTEM
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getThreadId() { return threadId; }
    public void setThreadId(String threadId) { this.threadId = threadId; }
    
    public MessageRole getRole() { return role; }
    public void setRole(MessageRole role) { this.role = role; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public String getCitations() { return citations; }
    public void setCitations(String citations) { this.citations = citations; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
