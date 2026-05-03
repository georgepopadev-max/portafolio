package com.incident.assistant.llm;

import com.incident.assistant.dto.CitationDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RealLlmClient implements LlmClient {

    private final String apiKey;
    private final String endpoint;

    public RealLlmClient() {
        this.apiKey = System.getenv("OPENAI_API_KEY");
        this.endpoint = System.getenv("OPENAI_ENDPOINT");
    }

    @Override
    public String generate(String prompt, List<CitationDto> citations) {
        throw new UnsupportedOperationException(
            "Real LLM integration requires API key configuration. " +
            "Set OPENAI_API_KEY environment variable to enable."
        );
    }
}
