package com.incident.assistant.dto;

public class ResolutionRequest {
    private String threadId;
    private String summary;
    private String steps;
    private String outcome;
    private String jiraTicketId;
    private String resolvedBy;

    public ResolutionRequest() {}

    public String getThreadId() { return threadId; }
    public void setThreadId(String threadId) { this.threadId = threadId; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public String getSteps() { return steps; }
    public void setSteps(String steps) { this.steps = steps; }

    public String getOutcome() { return outcome; }
    public void setOutcome(String outcome) { this.outcome = outcome; }

    public String getJiraTicketId() { return jiraTicketId; }
    public void setJiraTicketId(String jiraTicketId) { this.jiraTicketId = jiraTicketId; }

    public String getResolvedBy() { return resolvedBy; }
    public void setResolvedBy(String resolvedBy) { this.resolvedBy = resolvedBy; }
}
