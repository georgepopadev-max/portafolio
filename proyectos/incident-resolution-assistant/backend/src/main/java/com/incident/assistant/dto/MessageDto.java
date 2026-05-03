package com.incident.assistant.dto;

import java.time.LocalDateTime;

public class MessageDto {
    private String id;
    private String role;
    private String content;
    private String citations;
    private LocalDateTime createdAt;

    public MessageDto() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getCitations() { return citations; }
    public void setCitations(String citations) { this.citations = citations; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
