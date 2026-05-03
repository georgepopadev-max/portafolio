package com.incident.assistant.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.incident.assistant.dto.*;
import com.incident.assistant.llm.LlmClient;
import com.incident.assistant.model.ChatMessage;
import com.incident.assistant.model.IncidentThread;
import com.incident.assistant.rag.RetrievalService;
import com.incident.assistant.repository.ChatMessageRepository;
import com.incident.assistant.repository.IncidentThreadRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatService {

    private final ChatMessageRepository messageRepository;
    private final IncidentThreadRepository threadRepository;
    private final RetrievalService retrievalService;
    private final LlmClient llmClient;
    private final ObjectMapper objectMapper;

    public ChatService(ChatMessageRepository messageRepository,
                      IncidentThreadRepository threadRepository,
                      RetrievalService retrievalService,
                      LlmClient llmClient) {
        this.messageRepository = messageRepository;
        this.threadRepository = threadRepository;
        this.retrievalService = retrievalService;
        this.llmClient = llmClient;
        this.objectMapper = new ObjectMapper();
    }

    public ChatResponse processMessage(ChatRequest request) {
        String userMessage = request.getMessage();

        IncidentThread thread;
        final String finalThreadId;
        if (request.getThreadId() == null || request.getThreadId().isEmpty()) {
            thread = new IncidentThread(extractTitle(userMessage));
            thread.setCategory(detectCategory(userMessage));
            thread = threadRepository.save(thread);
            finalThreadId = thread.getId();
        } else {
            finalThreadId = request.getThreadId();
            thread = threadRepository.findById(finalThreadId)
                .orElseThrow(() -> new IllegalArgumentException("Thread not found: " + finalThreadId));
        }

        ChatMessage userMsg = new ChatMessage(finalThreadId, ChatMessage.MessageRole.USER, userMessage);
        messageRepository.save(userMsg);

        List<CitationDto> citations = retrievalService.retrieve(userMessage, 5);

        String prompt = llmClient.buildPrompt(userMessage, citations);
        String responseContent = llmClient.generate(prompt, citations);

        ChatMessage assistantMsg = new ChatMessage(finalThreadId, ChatMessage.MessageRole.ASSISTANT, responseContent);
        try {
            assistantMsg.setCitations(objectMapper.writeValueAsString(citations));
        } catch (Exception e) {
            assistantMsg.setCitations("[]");
        }
        messageRepository.save(assistantMsg);

        return new ChatResponse(responseContent, citations, finalThreadId);
    }

    public List<ThreadDto> getAllThreads() {
        return threadRepository.findAllByOrderByCreatedAtDesc()
            .stream()
            .map(this::toThreadDto)
            .collect(Collectors.toList());
    }

    public List<MessageDto> getThreadMessages(String threadId) {
        return messageRepository.findByThreadIdOrderByCreatedAtAsc(threadId)
            .stream()
            .map(this::toMessageDto)
            .collect(Collectors.toList());
    }

    public IncidentThread getThread(String threadId) {
        return threadRepository.findById(threadId)
            .orElseThrow(() -> new IllegalArgumentException("Thread not found: " + threadId));
    }

    private String extractTitle(String message) {
        if (message.length() <= 50) {
            return message;
        }
        return message.substring(0, 47) + "...";
    }

    private String detectCategory(String message) {
        String lower = message.toLowerCase();
        if (lower.contains("database") || lower.contains("db") || lower.contains("sql")) {
            return "database";
        }
        if (lower.contains("memory") || lower.contains("leak") || lower.contains("oom")) {
            return "memory";
        }
        if (lower.contains("network") || lower.contains("connection") || lower.contains("timeout")) {
            return "network";
        }
        if (lower.contains("service") || lower.contains("api") || lower.contains("pod")) {
            return "application";
        }
        return "general";
    }

    private ThreadDto toThreadDto(IncidentThread thread) {
        ThreadDto dto = new ThreadDto();
        dto.setId(thread.getId());
        dto.setTitle(thread.getTitle());
        dto.setStatus(thread.getStatus().name());
        dto.setCategory(thread.getCategory());
        dto.setCreatedAt(thread.getCreatedAt());
        dto.setResolvedAt(thread.getResolvedAt());
        return dto;
    }

    private MessageDto toMessageDto(ChatMessage message) {
        MessageDto dto = new MessageDto();
        dto.setId(message.getId());
        dto.setRole(message.getRole().name());
        dto.setContent(message.getContent());
        dto.setCitations(message.getCitations());
        dto.setCreatedAt(message.getCreatedAt());
        return dto;
    }
}
