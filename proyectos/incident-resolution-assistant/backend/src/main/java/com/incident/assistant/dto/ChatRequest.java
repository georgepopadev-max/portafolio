package com.incident.assistant.dto;

public class ChatRequest {
    private String threadId;
    private String message;

    public ChatRequest() {}

    public ChatRequest(String threadId, String message) {
        this.threadId = threadId;
        this.message = message;
    }

    public String getThreadId() { return threadId; }
    public void setThreadId(String threadId) { this.threadId = threadId; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
