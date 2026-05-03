package com.apidocgen.service;

import com.apidocgen.dto.*;
import com.apidocgen.entity.Project;
import com.apidocgen.entity.SourceUpload;
import com.apidocgen.entity.GeneratedDoc;
import com.apidocgen.repository.ProjectRepository;
import com.apidocgen.repository.SourceUploadRepository;
import com.apidocgen.repository.GeneratedDocRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProjectService {
    
    private final ProjectRepository projectRepository;
    private final SourceUploadRepository sourceUploadRepository;
    private final GeneratedDocRepository generatedDocRepository;
    
    public ProjectService(ProjectRepository projectRepository, 
                          SourceUploadRepository sourceUploadRepository,
                          GeneratedDocRepository generatedDocRepository) {
        this.projectRepository = projectRepository;
        this.sourceUploadRepository = sourceUploadRepository;
        this.generatedDocRepository = generatedDocRepository;
    }
    
    public List<ProjectDto> getAllProjects() {
        return projectRepository.findAllByOrderByUpdatedAtDesc()
            .stream()
            .map(this::toDto)
            .collect(Collectors.toList());
    }
    
    public ProjectDto getProject(UUID id) {
        Project project = projectRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Project not found: " + id));
        return toDto(project);
    }
    
    @Transactional
    public ProjectDto createProject(CreateProjectRequest request) {
        Project project = new Project();
        project.setName(request.getName());
        project.setDescription(request.getDescription());
        project.setAiProvider(request.getAiProvider() != null ? request.getAiProvider() : "mock");
        
        project = projectRepository.save(project);
        return toDto(project);
    }
    
    @Transactional
    public ProjectDto updateProject(UUID id, String name, String description) {
        Project project = projectRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Project not found: " + id));
        
        if (name != null) project.setName(name);
        if (description != null) project.setDescription(description);
        project.setUpdatedAt(LocalDateTime.now());
        
        project = projectRepository.save(project);
        return toDto(project);
    }
    
    @Transactional
    public void deleteProject(UUID id) {
        projectRepository.deleteById(id);
    }
    
    @Transactional
    public SourceUploadDto addSourceUpload(UUID projectId, String filename, long fileSize, String sourceCode) {
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new IllegalArgumentException("Project not found: " + projectId));
        
        SourceUpload upload = new SourceUpload();
        upload.setProject(project);
        upload.setFilename(filename);
        upload.setFileSize(fileSize);
        upload.setSourceCode(sourceCode);
        upload.setControllerCount(0);
        upload.setEndpointCount(0);
        
        upload = sourceUploadRepository.save(upload);
        project.setUpdatedAt(LocalDateTime.now());
        projectRepository.save(project);
        
        return toUploadDto(upload);
    }
    
    public List<SourceUploadDto> getProjectUploads(UUID projectId) {
        return sourceUploadRepository.findByProjectId(projectId)
            .stream()
            .map(this::toUploadDto)
            .collect(Collectors.toList());
    }
    
    public List<GeneratedDocDto> getProjectDocs(UUID projectId) {
        return generatedDocRepository.findByProjectIdOrderByVersionDesc(projectId)
            .stream()
            .map(this::toGeneratedDocDto)
            .collect(Collectors.toList());
    }
    
    public GeneratedDocDto getLatestDoc(UUID projectId) {
        return generatedDocRepository.findFirstByProjectIdOrderByVersionDesc(projectId)
            .map(this::toGeneratedDocDto)
            .orElse(null);
    }
    
    private ProjectDto toDto(Project project) {
        int endpointCount = 0;
        String lastGenerated = null;
        GeneratedDoc latest = generatedDocRepository.findFirstByProjectIdOrderByVersionDesc(project.getId())
            .orElse(null);
        
        if (latest != null) {
            endpointCount = countEndpoints(latest.getSpecYaml());
            lastGenerated = latest.getGeneratedAt().toString();
        }
        
        return new ProjectDto(
            project.getId(),
            project.getName(),
            project.getDescription(),
            endpointCount,
            "READY",
            lastGenerated,
            project.getAiProvider()
        );
    }
    
    private int countEndpoints(String yaml) {
        if (yaml == null) return 0;
        return yaml.split("paths:").length - 1;
    }
    
    private SourceUploadDto toUploadDto(SourceUpload upload) {
        return new SourceUploadDto(
            upload.getId(),
            upload.getFilename(),
            upload.getFileSize(),
            upload.getUploadedAt(),
            upload.getControllerCount(),
            upload.getEndpointCount()
        );
    }
    
    private GeneratedDocDto toGeneratedDocDto(GeneratedDoc doc) {
        GeneratedDocDto dto = new GeneratedDocDto();
        dto.setId(doc.getId());
        dto.setProjectId(doc.getProject().getId());
        dto.setVersion(doc.getVersion());
        dto.setSpecYaml(doc.getSpecYaml());
        dto.setGeneratedAt(doc.getGeneratedAt());
        dto.setModelUsed(doc.getModelUsed());
        dto.setStatus(doc.getStatus());
        return dto;
    }
}