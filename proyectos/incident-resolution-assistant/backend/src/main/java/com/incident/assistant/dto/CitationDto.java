package com.incident.assistant.dto;

public class CitationDto {
    private String documentId;
    private String title;
    private String sourceType;
    private double relevanceScore;
    private String snippet;

    public CitationDto() {}

    public CitationDto(String documentId, String title, String sourceType, double relevanceScore, String snippet) {
        this.documentId = documentId;
        this.title = title;
        this.sourceType = sourceType;
        this.relevanceScore = relevanceScore;
        this.snippet = snippet;
    }

    public String getDocumentId() { return documentId; }
    public void setDocumentId(String documentId) { this.documentId = documentId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSourceType() { return sourceType; }
    public void setSourceType(String sourceType) { this.sourceType = sourceType; }

    public double getRelevanceScore() { return relevanceScore; }
    public void setRelevanceScore(double relevanceScore) { this.relevanceScore = relevanceScore; }

    public String getSnippet() { return snippet; }
    public void setSnippet(String snippet) { this.snippet = snippet; }
}
