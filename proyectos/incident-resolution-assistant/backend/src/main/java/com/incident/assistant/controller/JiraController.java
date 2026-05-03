package com.incident.assistant.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class JiraController {

    @PostMapping("/jira/ticket")
    public ResponseEntity<Map<String, String>> createJiraTicket(@RequestBody Map<String, String> ticketRequest) {
        String summary = ticketRequest.getOrDefault("summary", "");
        String description = ticketRequest.getOrDefault("description", "");
        String threadId = ticketRequest.getOrDefault("threadId", "");

        String ticketId = "JIRA-" + (int)(Math.random() * 9000 + 1000);

        return ResponseEntity.ok(Map.of(
            "ticketId", ticketId,
            "status", "created",
            "summary", summary,
            "threadId", threadId,
            "message", "Jira ticket created successfully (simulated)"
        ));
    }
}
