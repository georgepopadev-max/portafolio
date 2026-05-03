package com.apidocgen.controller;

import com.apidocgen.dto.*;
import com.apidocgen.entity.GeneratedDoc;
import com.apidocgen.service.GenerationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/generation")
@CrossOrigin(origins = "*")
public class GenerationController {
    
    private final GenerationService generationService;
    
    public GenerationController(GenerationService generationService) {
        this.generationService = generationService;
    }
    
    @PostMapping("/projects/{projectId}/generate")
    public ResponseEntity<GeneratedDocDto> startGeneration(
            @PathVariable UUID projectId,
            @RequestBody(required = false) GenerationParamsDto params) {
        
        if (params == null) {
            params = new GenerationParamsDto();
        }
        
        GeneratedDoc doc = generationService.generateDocumentation(projectId, params);
        
        GeneratedDocDto dto = new GeneratedDocDto();
        dto.setId(doc.getId());
        dto.setProjectId(projectId);
        dto.setVersion(doc.getVersion());
        dto.setSpecYaml(doc.getSpecYaml());
        dto.setGeneratedAt(doc.getGeneratedAt());
        dto.setModelUsed(doc.getModelUsed());
        dto.setStatus(doc.getStatus());
        
        return ResponseEntity.ok(dto);
    }
    
    @GetMapping("/projects/{projectId}/status")
    public GenerationStatusDto getStatus(@PathVariable UUID projectId) {
        return generationService.getGenerationStatus(projectId);
    }
}