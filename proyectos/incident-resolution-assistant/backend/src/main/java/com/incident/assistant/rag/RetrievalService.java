package com.incident.assistant.rag;

import com.incident.assistant.dto.CitationDto;
import com.incident.assistant.model.Document;
import com.incident.assistant.model.DocumentChunk;
import com.incident.assistant.repository.DocumentChunkRepository;
import com.incident.assistant.repository.DocumentRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RetrievalService {

    private final DocumentRepository documentRepository;
    private final DocumentChunkRepository chunkRepository;
    private final EmbeddingService embeddingService;

    public RetrievalService(DocumentRepository documentRepository,
                          DocumentChunkRepository chunkRepository,
                          EmbeddingService embeddingService) {
        this.documentRepository = documentRepository;
        this.chunkRepository = chunkRepository;
        this.embeddingService = embeddingService;
    }

    public List<CitationDto> retrieve(String query, int topK) {
        double[] queryEmbedding = embeddingService.embed(query);

        List<Document> allDocs = documentRepository.findAll();
        List<CitationWithScore> citationsWithScores = new ArrayList<>();

        for (Document doc : allDocs) {
            double[] docEmbedding = embeddingService.embed(doc.getContent());
            double similarity = embeddingService.cosineSimilarity(queryEmbedding, docEmbedding);

            String snippet = extractRelevantSnippet(doc.getContent(), query);

            citationsWithScores.add(new CitationWithScore(
                doc.getId(),
                doc.getTitle(),
                doc.getSourceType(),
                similarity,
                snippet
            ));
        }

        return citationsWithScores.stream()
            .filter(c -> c.score >= 0.1)
            .sorted(Comparator.comparingDouble(CitationWithScore::getScore).reversed())
            .limit(topK)
            .map(c -> new CitationDto(
                c.documentId,
                c.title,
                c.sourceType,
                c.score,
                c.snippet
            ))
            .collect(Collectors.toList());
    }

    private String extractRelevantSnippet(String content, String query) {
        String[] sentences = content.split("[.!?]+");
        String queryLower = query.toLowerCase();
        String[] queryTerms = queryLower.split("\\s+");

        String bestSnippet = "";
        double bestScore = 0;

        for (String sentence : sentences) {
            String sentenceLower = sentence.toLowerCase();
            double score = 0;
            for (String term : queryTerms) {
                if (sentenceLower.contains(term)) {
                    score++;
                }
            }
            if (score > bestScore) {
                bestScore = score;
                bestSnippet = sentence.trim();
            }
        }

        if (bestSnippet.isEmpty() && sentences.length > 0) {
            bestSnippet = sentences[0].trim();
        }

        return bestSnippet.length() > 200 ? bestSnippet.substring(0, 200) + "..." : bestSnippet;
    }

    public void chunkDocument(Document document) {
        String content = document.getContent();
        int chunkSize = 500;
        int overlap = 50;

        List<String> chunks = new ArrayList<>();
        int index = 0;

        while (index < content.length()) {
            int end = Math.min(index + chunkSize, content.length());
            String chunk = content.substring(index, end);

            if (end < content.length()) {
                int lastPeriod = chunk.lastIndexOf('.');
                int lastNewline = chunk.lastIndexOf('\n');
                int breakpoint = Math.max(lastPeriod, lastNewline);
                if (breakpoint > index + chunkSize / 2) {
                    end = index + breakpoint + 1;
                    chunk = content.substring(index, end);
                }
            }

            chunks.add(chunk.trim());
            index = end - overlap;
            if (index < 0) index = end;
        }

        int position = 0;
        for (String chunkText : chunks) {
            if (!chunkText.isEmpty()) {
                DocumentChunk chunk = new DocumentChunk(document.getId(), chunkText, position++);
                chunkRepository.save(chunk);
            }
        }
    }

    private static class CitationWithScore {
        final String documentId;
        final String title;
        final String sourceType;
        final double score;
        final String snippet;

        CitationWithScore(String documentId, String title, String sourceType, double score, String snippet) {
            this.documentId = documentId;
            this.title = title;
            this.sourceType = sourceType;
            this.score = score;
            this.snippet = snippet;
        }

        double getScore() { return score; }
    }
}
