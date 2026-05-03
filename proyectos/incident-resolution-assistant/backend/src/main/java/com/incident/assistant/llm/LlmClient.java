package com.incident.assistant.llm;

import com.incident.assistant.dto.CitationDto;
import java.util.List;

public interface LlmClient {

    String generate(String prompt, List<CitationDto> citations);

    default String buildPrompt(String userMessage, List<CitationDto> citations) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("You are an expert Incident Resolution Assistant helping DevOps engineers.\n\n");

        if (!citations.isEmpty()) {
            prompt.append("Based on the following retrieved context, provide a helpful response:\n\n");
            for (int i = 0; i < citations.size(); i++) {
                CitationDto citation = citations.get(i);
                prompt.append(String.format("[%d] %s (%s) - Relevance: %.0f%%\n%s\n\n",
                    i + 1,
                    citation.getTitle(),
                    citation.getSourceType(),
                    citation.getRelevanceScore() * 100,
                    citation.getSnippet()
                ));
            }
        }

        prompt.append("User query: ").append(userMessage).append("\n\n");
        prompt.append("Provide a clear, actionable response based on the context above.");

        return prompt.toString();
    }
}
