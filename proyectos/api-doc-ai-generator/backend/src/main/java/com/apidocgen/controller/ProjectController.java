package com.apidocgen.controller;

import com.apidocgen.dto.*;
import com.apidocgen.service.ProjectService;
import com.apidocgen.service.GenerationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/projects")
@CrossOrigin(origins = "*")
public class ProjectController {
    
    private final ProjectService projectService;
    private final GenerationService generationService;
    
    public ProjectController(ProjectService projectService, GenerationService generationService) {
        this.projectService = projectService;
        this.generationService = generationService;
    }
    
    @GetMapping
    public List<ProjectDto> getAllProjects() {
        return projectService.getAllProjects();
    }
    
    @GetMapping("/{id}")
    public ProjectDto getProject(@PathVariable UUID id) {
        return projectService.getProject(id);
    }
    
    @PostMapping
    public ProjectDto createProject(@RequestBody CreateProjectRequest request) {
        return projectService.createProject(request);
    }
    
    @PutMapping("/{id}")
    public ProjectDto updateProject(@PathVariable UUID id, 
                                    @RequestParam(required = false) String name,
                                    @RequestParam(required = false) String description) {
        return projectService.updateProject(id, name, description);
    }
    
    @DeleteMapping("/{id}")
    public void deleteProject(@PathVariable UUID id) {
        projectService.deleteProject(id);
    }
    
    @PostMapping("/{id}/upload")
    public SourceUploadDto uploadSource(@PathVariable UUID id, 
                                       @RequestBody SourceUploadRequest request) {
        return projectService.addSourceUpload(id, request.filename(), request.fileSize(), request.sourceCode());
    }
    
    @GetMapping("/{id}/uploads")
    public List<SourceUploadDto> getUploads(@PathVariable UUID id) {
        return projectService.getProjectUploads(id);
    }
    
    @GetMapping("/{id}/docs")
    public List<GeneratedDocDto> getDocs(@PathVariable UUID id) {
        return projectService.getProjectDocs(id);
    }
    
    @GetMapping("/{id}/docs/latest")
    public GeneratedDocDto getLatestDoc(@PathVariable UUID id) {
        return projectService.getLatestDoc(id);
    }
}

record SourceUploadRequest(String filename, long fileSize, String sourceCode) {}