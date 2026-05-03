package com.apidocgen.controller;

import com.apidocgen.service.ExportService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/export")
@CrossOrigin(origins = "*")
public class ExportController {
    
    private final ExportService exportService;
    
    public ExportController(ExportService exportService) {
        this.exportService = exportService;
    }
    
    @GetMapping("/docs/{docId}/yaml")
    public ResponseEntity<String> exportYaml(@PathVariable UUID docId) {
        String yaml = exportService.exportAsYaml(docId);
        
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"openapi.yaml\"")
            .contentType(MediaType.parseMediaType("text/yaml"))
            .body(yaml);
    }
    
    @GetMapping("/docs/{docId}/json")
    public ResponseEntity<String> exportJson(@PathVariable UUID docId) {
        String json = exportService.exportAsJson(docId);
        
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"openapi.json\"")
            .contentType(MediaType.APPLICATION_JSON)
            .body(json);
    }
    
    @PutMapping("/docs/{docId}")
    public ResponseEntity<String> updateSpec(@PathVariable UUID docId, @RequestBody String yaml) {
        String result = exportService.updateSpec(docId, yaml);
        return ResponseEntity.ok(result);
    }
    
    @PostMapping("/validate")
    public ResponseEntity<String> validateYaml(@RequestBody String yaml) {
        String result = exportService.validateYaml(yaml);
        return ResponseEntity.ok(result);
    }
}