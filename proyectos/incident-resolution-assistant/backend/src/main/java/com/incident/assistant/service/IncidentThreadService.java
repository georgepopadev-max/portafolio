package com.incident.assistant.service;

import com.incident.assistant.model.IncidentThread;
import com.incident.assistant.repository.IncidentThreadRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class IncidentThreadService {

    private final IncidentThreadRepository threadRepository;

    public IncidentThreadService(IncidentThreadRepository threadRepository) {
        this.threadRepository = threadRepository;
    }

    public List<IncidentThread> getAllThreads() {
        return threadRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<IncidentThread> getActiveThreads() {
        return threadRepository.findByStatusOrderByCreatedAtDesc(IncidentThread.ThreadStatus.ACTIVE);
    }

    public IncidentThread getThread(String threadId) {
        return threadRepository.findById(threadId)
            .orElseThrow(() -> new IllegalArgumentException("Thread not found: " + threadId));
    }

    public IncidentThread saveThread(IncidentThread thread) {
        return threadRepository.save(thread);
    }

    public void updateStatus(String threadId, IncidentThread.ThreadStatus status) {
        IncidentThread thread = getThread(threadId);
        thread.setStatus(status);
        if (status == IncidentThread.ThreadStatus.RESOLVED) {
            thread.setResolvedAt(java.time.LocalDateTime.now());
        }
        threadRepository.save(thread);
    }
}
