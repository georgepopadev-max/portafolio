package com.incident.assistant.service;

import com.incident.assistant.dto.ResolutionRequest;
import com.incident.assistant.model.IncidentThread;
import com.incident.assistant.model.Resolution;
import com.incident.assistant.repository.ResolutionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ResolutionService {

    private final ResolutionRepository resolutionRepository;
    private final IncidentThreadService threadService;

    public ResolutionService(ResolutionRepository resolutionRepository,
                           IncidentThreadService threadService) {
        this.resolutionRepository = resolutionRepository;
        this.threadService = threadService;
    }

    public Resolution createResolution(ResolutionRequest request) {
        IncidentThread thread = threadService.getThread(request.getThreadId());

        Resolution resolution = new Resolution(
            request.getThreadId(),
            request.getSummary(),
            request.getSteps()
        );
        resolution.setJiraTicketId(request.getJiraTicketId());
        resolution.setResolvedBy(request.getResolvedBy());

        resolution = resolutionRepository.save(resolution);

        thread.setStatus(IncidentThread.ThreadStatus.RESOLVED);
        thread.setResolvedAt(LocalDateTime.now());
        thread.setOutcome(request.getOutcome());
        threadService.saveThread(thread);

        return resolution;
    }

    public Resolution getResolution(String threadId) {
        return resolutionRepository.findByThreadId(threadId)
            .orElse(null);
    }
}
