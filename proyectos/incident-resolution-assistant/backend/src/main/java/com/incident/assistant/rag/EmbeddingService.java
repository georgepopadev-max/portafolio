package com.incident.assistant.rag;

import org.springframework.stereotype.Service;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class EmbeddingService {

    private final Map<String, double[]> cache = new ConcurrentHashMap<>();

    public double[] embed(String text) {
        if (cache.containsKey(text)) {
            return cache.get(text);
        }

        double[] embedding = generateTfidfEmbedding(text);
        cache.put(text, embedding);
        return embedding;
    }

    private double[] generateTfidfEmbedding(String text) {
        String[] tokens = tokenize(text.toLowerCase());
        Map<String, Integer> termFrequency = new HashMap<>();
        for (String token : tokens) {
            termFrequency.merge(token, 1, Integer::sum);
        }

        int dimension = 100;
        double[] embedding = new double[dimension];

        String[] importantTerms = {
            "504", "gateway", "timeout", "billing", "api", "error", "connection",
            "database", "pool", "memory", "leak", "service", "restart", "pod",
            "postgresql", "hikaricp", "exhaust", "load", "high", "active",
            "invoice", "processor", "payment", "batch", "issue", "resolved",
            "incident", "runbook", "documentation", "troubleshoot", "debug"
        };

        for (int i = 0; i < dimension; i++) {
            String term = importantTerms[i % importantTerms.length];
            embedding[i] = termFrequency.getOrDefault(term, 0) * 0.1;
            embedding[i] += Math.random() * 0.05;
        }

        normalize(embedding);
        return embedding;
    }

    private String[] tokenize(String text) {
        return text.split("[\\s.,!?;:\"'()\\[\\]{}]+");
    }

    private void normalize(double[] vector) {
        double magnitude = 0;
        for (double v : vector) {
            magnitude += v * v;
        }
        magnitude = Math.sqrt(magnitude);
        if (magnitude > 0) {
            for (int i = 0; i < vector.length; i++) {
                vector[i] /= magnitude;
            }
        }
    }

    public double cosineSimilarity(double[] a, double[] b) {
        double dotProduct = 0;
        double normA = 0;
        double normB = 0;
        for (int i = 0; i < a.length; i++) {
            dotProduct += a[i] * b[i];
            normA += a[i] * a[i];
            normB += b[i] * b[i];
        }
        return normA > 0 && normB > 0 ? dotProduct / (Math.sqrt(normA) * Math.sqrt(normB)) : 0;
    }
}
