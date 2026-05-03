package com.apidocgen.ai.provider;

import com.apidocgen.ai.interface.AIProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;

@Component
public class RealAIProvider implements AIProvider {
    
    private static final Logger log = LoggerFactory.getLogger(RealAIProvider.class);
    
    @Value("${ai.openai.api-key:}")
    private String apiKey;
    
    @Value("${ai.openai.model:gpt-4}")
    private String model;
    
    @Value("${ai.openai.base-url:https://api.openai.com/v1}")
    private String baseUrl;
    
    private static final String OPENAI_COMPLETION_URL = "/chat/completions";
    
    @Override
    public String generateEndpointDescription(String controllerName, String method, String path,
                                              String httpMethod, List<String> paramTypes,
                                              String returnType, String detailLevel) {
        if (!isAvailable()) {
            throw new IllegalStateException("OpenAI API key not configured");
        }
        
        String prompt = buildEndpointPrompt(controllerName, method, path, httpMethod, paramTypes, returnType, detailLevel);
        return callOpenAI(prompt);
    }
    
    @Override
    public String generateSchemaDescription(String className, List<String> fieldNames,
                                            List<String> fieldTypes, String detailLevel) {
        if (!isAvailable()) {
            throw new IllegalStateException("OpenAI API key not configured");
        }
        
        String prompt = buildSchemaPrompt(className, fieldNames, fieldTypes, detailLevel);
        return callOpenAI(prompt);
    }
    
    @Override
    public String generateExampleValue(String fieldName, String fieldType) {
        if (!isAvailable()) {
            throw new IllegalStateException("OpenAI API key not configured");
        }
        
        String prompt = String.format(
            "Generate a realistic example JSON value for a field named '%s' with type '%s'. " +
            "Return ONLY the value in valid JSON format, no explanation.",
            fieldName, fieldType
        );
        return callOpenAI(prompt);
    }
    
    private String buildEndpointPrompt(String controllerName, String method, String path,
                                       String httpMethod, List<String> paramTypes,
                                       String returnType, String detailLevel) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Generate a clear, concise API endpoint description for:\n\n");
        prompt.append("Controller: ").append(controllerName).append("\n");
        prompt.append("Method: ").append(method).append("\n");
        prompt.append("HTTP Method: ").append(httpMethod).append("\n");
        prompt.append("Path: ").append(path).append("\n");
        if (!paramTypes.isEmpty()) {
            prompt.append("Parameters: ").append(String.join(", ", paramTypes)).append("\n");
        }
        prompt.append("Return Type: ").append(returnType).append("\n");
        prompt.append("Detail Level: ").append(detailLevel).append("\n\n");
        prompt.append("Return ONLY the description text, no markdown or formatting.");
        
        return prompt.toString();
    }
    
    private String buildSchemaPrompt(String className, List<String> fieldNames,
                                     List<String> fieldTypes, String detailLevel) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Generate a schema description for a ").append(className).append(" with fields:\n");
        
        for (int i = 0; i < fieldNames.size(); i++) {
            prompt.append("- ").append(fieldNames.get(i)).append(": ").append(fieldTypes.get(i)).append("\n");
        }
        
        prompt.append("\nDetail Level: ").append(detailLevel).append("\n");
        prompt.append("Return ONLY the description text, no markdown.");
        
        return prompt.toString();
    }
    
    private String callOpenAI(String prompt) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + OPENAI_COMPLETION_URL))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(buildRequestBody(prompt)))
                .build();
            
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, 
                HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                return parseOpenAIResponse(response.body());
            } else {
                log.error("OpenAI API error: {}", response.body());
                throw new RuntimeException("OpenAI API error: " + response.statusCode());
            }
        } catch (Exception e) {
            log.error("Error calling OpenAI API", e);
            throw new RuntimeException("Failed to call OpenAI API", e);
        }
    }
    
    private String buildRequestBody(String prompt) {
        return String.format(
            "{\"model\":\"%s\",\"messages\":[{\"role\":\"user\",\"content\":\"%s\"}],\"temperature\":0.7,\"max_tokens\":500}",
            model, prompt.replace("\"", "\\\"")
        );
    }
    
    private String parseOpenAIResponse(String responseBody) {
        return responseBody;
    }
    
    @Override
    public boolean isAvailable() {
        return apiKey != null && !apiKey.isEmpty();
    }
}