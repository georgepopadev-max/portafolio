package com.incident.assistant.dto;

import java.util.List;

public class ChatResponse {
    private String message;
    private List<CitationDto> citations;
    private String threadId;

    public ChatResponse() {}

    public ChatResponse(String message, List<CitationDto> citations, String threadId) {
        this.message = message;
        this.citations = citations;
        this.threadId = threadId;
    }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public List<CitationDto> getCitations() { return citations; }
    public void setCitations(List<CitationDto> citations) { this.citations = citations; }

    public String getThreadId() { return threadId; }
    public void setThreadId(String threadId) { this.threadId = threadId; }
}
