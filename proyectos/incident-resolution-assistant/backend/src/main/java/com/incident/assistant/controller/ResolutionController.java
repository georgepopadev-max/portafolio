package com.incident.assistant.controller;

import com.incident.assistant.dto.ResolutionRequest;
import com.incident.assistant.model.Resolution;
import com.incident.assistant.service.ResolutionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ResolutionController {

    private final ResolutionService resolutionService;

    public ResolutionController(ResolutionService resolutionService) {
        this.resolutionService = resolutionService;
    }

    @PostMapping("/resolutions")
    public ResponseEntity<Resolution> createResolution(@RequestBody ResolutionRequest request) {
        Resolution resolution = resolutionService.createResolution(request);
        return ResponseEntity.ok(resolution);
    }

    @GetMapping("/threads/{threadId}/resolution")
    public ResponseEntity<Resolution> getResolution(@PathVariable String threadId) {
        Resolution resolution = resolutionService.getResolution(threadId);
        if (resolution == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(resolution);
    }
}
