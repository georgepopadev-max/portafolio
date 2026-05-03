package com.incident.assistant.controller;

import com.incident.assistant.dto.ChatRequest;
import com.incident.assistant.dto.ChatResponse;
import com.incident.assistant.dto.MessageDto;
import com.incident.assistant.dto.ThreadDto;
import com.incident.assistant.service.ChatService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/chat")
    public ChatResponse sendMessage(@RequestBody ChatRequest request) {
        return chatService.processMessage(request);
    }

    @GetMapping("/threads")
    public List<ThreadDto> getAllThreads() {
        return chatService.getAllThreads();
    }

    @GetMapping("/threads/{threadId}/messages")
    public List<MessageDto> getThreadMessages(@PathVariable String threadId) {
        return chatService.getThreadMessages(threadId);
    }
}
