package com.apidocgen.service;

import com.apidocgen.ai.AIService;
import com.apidocgen.ai.interface.AIProvider;
import com.apidocgen.dto.GenerationParamsDto;
import com.apidocgen.entity.GeneratedDoc;
import com.apidocgen.entity.Project;
import com.apidocgen.parser.JavaCodeParser;
import com.apidocgen.parser.ParseResult;
import com.apidocgen.repository.GeneratedDocRepository;
import com.apidocgen.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GenerationServiceTest {
    
    @Mock
    private ProjectRepository projectRepository;
    
    @Mock
    private GeneratedDocRepository generatedDocRepository;
    
    @Mock
    private AIProvider aiProvider;
    
    private JavaCodeParser codeParser;
    private AIService aiService;
    private GenerationService generationService;
    
    @BeforeEach
    void setUp() {
        codeParser = new JavaCodeParser();
        aiService = new AIService(
            new com.apidocgen.ai.provider.MockAIProvider(),
            new com.apidocgen.ai.provider.RealAIProvider()
        );
        generationService = new GenerationService(
            projectRepository, 
            generatedDocRepository, 
            codeParser, 
            aiService
        );
    }
    
    @Test
    void generateDocumentation_withValidProject_createsDoc() {
        UUID projectId = UUID.randomUUID();
        
        Project project = new Project();
        project.setId(projectId);
        project.setName("Test Project");
        project.setAiProvider("mock");
        
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(generatedDocRepository.findFirstByProjectIdOrderByVersionDesc(projectId))
            .thenReturn(Optional.empty());
        when(generatedDocRepository.save(any(GeneratedDoc.class)))
            .thenAnswer(inv -> {
                GeneratedDoc doc = inv.getArgument(0);
                doc.setId(UUID.randomUUID());
                return doc;
            });
        
        GenerationParamsDto params = new GenerationParamsDto(0.7, 2000, "standard", "mock-gpt-4");
        GeneratedDoc result = generationService.generateDocumentation(projectId, params);
        
        assertNotNull(result);
        assertEquals(1, result.getVersion());
        assertNotNull(result.getSpecYaml());
        assertTrue(result.getSpecYaml().contains("openapi: 3.0.3"));
        assertTrue(result.getSpecYaml().contains("paths:"));
        
        verify(generatedDocRepository).save(any(GeneratedDoc.class));
    }
    
    @Test
    void generateDocumentation_withExistingDocs_incrementsVersion() {
        UUID projectId = UUID.randomUUID();
        
        Project project = new Project();
        project.setId(projectId);
        project.setName("Test Project");
        
        GeneratedDoc existingDoc = new GeneratedDoc();
        existingDoc.setVersion(3);
        
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(generatedDocRepository.findFirstByProjectIdOrderByVersionDesc(projectId))
            .thenReturn(Optional.of(existingDoc));
        when(generatedDocRepository.save(any(GeneratedDoc.class)))
            .thenAnswer(inv -> {
                GeneratedDoc doc = inv.getArgument(0);
                doc.setId(UUID.randomUUID());
                return doc;
            });
        
        GenerationParamsDto params = new GenerationParamsDto();
        GeneratedDoc result = generationService.generateDocumentation(projectId, params);
        
        assertEquals(4, result.getVersion());
    }
    
    @Test
    void generateDocumentation_withInvalidProjectId_throwsException() {
        UUID projectId = UUID.randomUUID();
        
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());
        
        assertThrows(IllegalArgumentException.class, () -> 
            generationService.generateDocumentation(projectId, new GenerationParamsDto())
        );
    }
    
    @Test
    void getGenerationStatus_idleProject_returnsIdleStatus() {
        UUID projectId = UUID.randomUUID();
        
        GenerationStatusDto status = generationService.getGenerationStatus(projectId);
        
        assertEquals("IDLE", status.getStatus());
    }
    
    @Test
    void generateDocumentation_producesValidOpenApiSpec() {
        UUID projectId = UUID.randomUUID();
        
        Project project = new Project();
        project.setId(projectId);
        
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(generatedDocRepository.findFirstByProjectIdOrderByVersionDesc(projectId))
            .thenReturn(Optional.empty());
        when(generatedDocRepository.save(any(GeneratedDoc.class)))
            .thenAnswer(inv -> {
                GeneratedDoc doc = inv.getArgument(0);
                doc.setId(UUID.randomUUID());
                return doc;
            });
        
        GeneratedDoc result = generationService.generateDocumentation(projectId, new GenerationParamsDto());
        
        String spec = result.getSpecYaml();
        assertTrue(spec.contains("openapi: 3.0.3"));
        assertTrue(spec.contains("info:"));
        assertTrue(spec.contains("title:"));
        assertTrue(spec.contains("paths:"));
        assertTrue(spec.contains("components:"));
        assertTrue(spec.contains("schemas:"));
    }
}