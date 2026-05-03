package com.incident.assistant.llm;

import com.incident.assistant.dto.CitationDto;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MockLlmClient implements LlmClient {

    private static final Random random = new Random();

    @Override
    public String generate(String prompt, List<CitationDto> citations) {
        String userMessage = extractUserMessage(prompt);
        return generateMockResponse(userMessage, citations);
    }

    private String extractUserMessage(String prompt) {
        int idx = prompt.lastIndexOf("User query:");
        if (idx >= 0) {
            return prompt.substring(idx + 11).trim();
        }
        return prompt;
    }

    private String generateMockResponse(String userMessage, List<CitationDto> citations) {
        String lowerMessage = userMessage.toLowerCase();

        if (lowerMessage.contains("504") || lowerMessage.contains("gateway") || lowerMessage.contains("timeout")) {
            return buildTimeoutResponse(citations);
        }

        if (lowerMessage.contains("database") || lowerMessage.contains("connection") || lowerMessage.contains("db")) {
            return buildDatabaseResponse(citations);
        }

        if (lowerMessage.contains("memory") || lowerMessage.contains("leak") || lowerMessage.contains("oom")) {
            return buildMemoryResponse(citations);
        }

        if (citations.isEmpty()) {
            return "I'm analyzing your incident. Could you provide more details about the affected service and any error messages you're seeing?";
        }

        StringBuilder response = new StringBuilder();
        response.append("Based on my analysis of the retrieved documentation:\n\n");

        for (CitationDto citation : citations) {
            response.append(String.format("**%s** (Relevance: %.0f%%)\n%s\n\n",
                citation.getTitle(),
                citation.getRelevanceScore() * 100,
                citation.getSnippet()
            ));
        }

        response.append("\n**Suggested Actions:**\n");
        response.append("- Review the cited documentation for detailed troubleshooting steps\n");
        response.append("- Check monitoring dashboards for affected metrics\n");
        response.append("- Consider escalating if the issue persists\n");

        return response.toString();
    }

    private String buildTimeoutResponse(List<CitationDto> citations) {
        StringBuilder response = new StringBuilder();
        response.append("🔍 **Incident Analysis: 504 Gateway Timeout**\n\n");
        response.append("Based on the retrieved context, 504 Gateway Timeout errors typically indicate:\n\n");
        response.append("1. **Upstream service timeout** - The backend service is taking too long to respond\n");
        response.append("2. **Connection pool exhaustion** - Database or API connection pool is full\n");
        response.append("3. **Memory pressure** - Service experiencing high memory usage leading to slow processing\n\n");

        response.append("**Immediate Actions:**\n");
        response.append("1. Check connection pool metrics (HikariCP max connections: 20)\n");
        response.append("2. Review PostgreSQL active connections with: `SELECT count(*) FROM pg_stat_activity`\n");
        response.append("3. Check billing-service pod memory: `kubectl top pods -n billing`\n\n");

        if (!citations.isEmpty()) {
            response.append("**Matching Documentation:**\n");
            for (CitationDto citation : citations) {
                if (citation.getRelevanceScore() > 0.5) {
                    response.append(String.format("- %s (%.0f%% match)\n",
                        citation.getTitle(),
                        citation.getRelevanceScore() * 100
                    ));
                }
            }
        }

        return response.toString();
    }

    private String buildDatabaseResponse(List<CitationDto> citations) {
        StringBuilder response = new StringBuilder();
        response.append("🔍 **Incident Analysis: Database Connection Issue**\n\n");
        response.append("Database connection issues can stem from:\n\n");
        response.append("1. **Connection pool exhaustion** - All available connections in use\n");
        response.append("2. **Long-running queries** - Queries blocking connection release\n");
        response.append("3. **Network connectivity** - Firewall or network interruptions\n\n");

        response.append("**Diagnostic Steps:**\n");
        response.append("1. Check active connections:\n");
        response.append("   ```sql\n   SELECT pid, state, query, query_start\n   FROM pg_stat_activity\n   WHERE datname = 'billing_db';\n   ```\n");
        response.append("2. Identify blocking queries:\n");
        response.append("   ```sql\n   SELECT * FROM pg_stat_activity\n   WHERE state = 'active' AND query_start < NOW() - INTERVAL '5 minutes';\n   ```\n");
        response.append("3. Check HikariCP pool stats in application metrics\n\n");

        return response.toString();
    }

    private String buildMemoryResponse(List<CitationDto> citations) {
        StringBuilder response = new StringBuilder();
        response.append("🔍 **Incident Analysis: Memory Leak Suspected**\n\n");
        response.append("Memory issues typically manifest as:\n\n");
        response.append("1. **Gradual memory growth** - Pod memory increases over time\n");
        response.append("2. **OOMKilled events** - Kubernetes terminating pods due to memory limits\n");
        response.append("3. **Increased GC activity** - Frequent garbage collection impacting performance\n\n");

        response.append("**Immediate Actions:**\n");
        response.append("1. Check pod memory usage: `kubectl top pods -n production`\n");
        response.append("2. Review pod events: `kubectl describe pod <pod-name> -n production`\n");
        response.append("3. Check for OOMKilled status in pod conditions\n");
        response.append("4. If confirmed, restart the affected pods as a temporary fix\n\n");

        response.append("**Long-term Resolution:**\n");
        response.append("- Profile the application to identify memory leaks\n");
        response.append("- Implement proper resource limits\n");
        response.append("- Set up memory-based autoscaling\n");

        return response.toString();
    }
}
