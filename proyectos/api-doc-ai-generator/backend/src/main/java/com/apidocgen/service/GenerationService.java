package com.apidocgen.service;

import com.apidocgen.dto.GenerationParamsDto;
import com.apidocgen.dto.GenerationStatusDto;
import com.apidocgen.entity.GeneratedDoc;
import com.apidocgen.entity.Project;
import com.apidocgen.parser.*;
import com.apidocgen.repository.GeneratedDocRepository;
import com.apidocgen.repository.ProjectRepository;
import com.apidocgen.ai.AIService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class GenerationService {
    
    private final ProjectRepository projectRepository;
    private final GeneratedDocRepository generatedDocRepository;
    private final JavaCodeParser codeParser;
    private final AIService aiService;
    
    @Value("${ai.mock.delay-ms:1500}")
    private int mockDelayMs;
    
    private final Map<UUID, GenerationStatusDto> generationStatuses = new ConcurrentHashMap<>();
    
    public GenerationService(ProjectRepository projectRepository,
                           GeneratedDocRepository generatedDocRepository,
                           JavaCodeParser codeParser,
                           AIService aiService) {
        this.projectRepository = projectRepository;
        this.generatedDocRepository = generatedDocRepository;
        this.codeParser = codeParser;
        this.aiService = aiService;
    }
    
    public GenerationStatusDto getGenerationStatus(UUID projectId) {
        return generationStatuses.getOrDefault(projectId, 
            new GenerationStatusDto("IDLE", "No generation in progress", 0, 4, 0, ""));
    }
    
    @Transactional
    public GeneratedDoc generateDocumentation(UUID projectId, GenerationParamsDto params) {
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new IllegalArgumentException("Project not found: " + projectId));
        
        updateStatus(projectId, "SCANNING", "Scanning source files", 1, 4, 0);
        
        String sourceCode = getProjectSourceCode(project);
        if (sourceCode == null || sourceCode.isEmpty()) {
            sourceCode = getDefaultSourceCode();
        }
        
        updateStatus(projectId, "PARSING", "Analyzing Spring annotations", 2, 4, 0);
        ParseResult parseResult = codeParser.parseSourceCode(sourceCode);
        
        updateStatus(projectId, "GENERATING", "Generating endpoint descriptions with AI", 3, 4, 
            parseResult.getTotalEndpoints());
        
        String openApiSpec = generateOpenApiSpec(parseResult, params);
        
        updateStatus(projectId, "FINALIZING", "Finalizing OpenAPI spec", 4, 4, 
            parseResult.getTotalEndpoints());
        
        int latestVersion = generatedDocRepository.findFirstByProjectIdOrderByVersionDesc(projectId)
            .map(GeneratedDoc::getVersion)
            .orElse(0);
        
        GeneratedDoc doc = new GeneratedDoc();
        doc.setProject(project);
        doc.setVersion(latestVersion + 1);
        doc.setSpecYaml(openApiSpec);
        doc.setSpecJson(convertYamlToJson(openApiSpec));
        doc.setGeneratedAt(LocalDateTime.now());
        doc.setModelUsed("mock-gpt-4".equals(params.getModel()) || params.getModel() == null 
            ? "Mock GPT-4" : params.getModel());
        doc.setStatus("COMPLETED");
        
        doc = generatedDocRepository.save(doc);
        
        project.setUpdatedAt(LocalDateTime.now());
        projectRepository.save(project);
        
        generationStatuses.put(projectId, new GenerationStatusDto(
            "COMPLETED", "Documentation generated successfully", 4, 4, 
            parseResult.getTotalEndpoints(), "Generated v" + doc.getVersion()
        ));
        
        return doc;
    }
    
    private void updateStatus(UUID projectId, String status, String step, int stepNum, 
                              int totalSteps, int aiRequests) {
        generationStatuses.put(projectId, new GenerationStatusDto(
            status, step, stepNum, totalSteps, aiRequests, ""
        ));
    }
    
    private String getProjectSourceCode(Project project) {
        return project.getUploads().stream()
            .filter(u -> u.getSourceCode() != null)
            .findFirst()
            .map(u -> u.getSourceCode())
            .orElse(null);
    }
    
    private String getDefaultSourceCode() {
        return getSampleInvoiceController() + "\n\n" + getSamplePaymentController() + "\n\n" + getSampleCustomerDto();
    }
    
    private String generateOpenApiSpec(ParseResult parseResult, GenerationParamsDto params) {
        StringBuilder yaml = new StringBuilder();
        yaml.append("openapi: 3.0.3\n");
        yaml.append("info:\n");
        yaml.append("  title: Generated API Documentation\n");
        yaml.append("  version: 1.0.0\n");
        yaml.append("  description: Auto-generated OpenAPI documentation for your API\n");
        yaml.append("servers:\n");
        yaml.append("  - url: http://localhost:8080\n");
        yaml.append("    description: Local development server\n");
        yaml.append("paths:\n");
        
        for (ParsedController controller : parseResult.getControllers()) {
            for (ParsedEndpoint endpoint : controller.getEndpoints()) {
                String description = aiService.generateEndpointDoc(
                    controller.getName(),
                    endpoint.getMethodName(),
                    endpoint.getPath(),
                    endpoint.getHttpMethod(),
                    endpoint.getParameters().stream()
                        .map(ParsedParameter::getType)
                        .collect(Collectors.toList()),
                    endpoint.getReturnType(),
                    params != null ? params.getDetailLevel() : "standard"
                );
                
                yaml.append("  ").append(endpoint.getPath()).append(":\n");
                yaml.append("    ").append(endpoint.getHttpMethod().toLowerCase()).append(":\n");
                yaml.append("      summary: \"").append(escapeYaml(endpoint.getMethodName().replaceAll("([A-Z])", " $1").trim())).append("\"\n");
                yaml.append("      description: \"").append(escapeYaml(description)).append("\"\n");
                yaml.append("      tags:\n");
                yaml.append("        - ").append(controller.getName().replace("Controller", "")).append("\n");
                
                if (!endpoint.getParameters().isEmpty()) {
                    yaml.append("      parameters:\n");
                    for (ParsedParameter param : endpoint.getParameters()) {
                        yaml.append("        - name: ").append(param.getName()).append("\n");
                        yaml.append("          in: query\n");
                        yaml.append("          required: ").append(param.isRequired()).append("\n");
                        yaml.append("          schema:\n");
                        yaml.append("            type: ").append(mapToJsonType(param.getType())).append("\n");
                    }
                }
                
                yaml.append("      responses:\n");
                yaml.append("        '200':\n");
                yaml.append("          description: Successful response\n");
                yaml.append("          content:\n");
                yaml.append("            application/json:\n");
                yaml.append("              schema:\n");
                yaml.append("                type: object\n");
                yaml.append("\n");
            }
        }
        
        yaml.append("components:\n");
        yaml.append("  schemas:\n");
        
        for (ParsedSchema schema : parseResult.getSchemas()) {
            yaml.append("    ").append(schema.getName()).append(":\n");
            yaml.append("      type: object\n");
            yaml.append("      properties:\n");
            
            for (ParsedField field : schema.getFields()) {
                yaml.append("        ").append(field.getName()).append(":\n");
                yaml.append("          type: ").append(mapToJsonType(field.getType())).append("\n");
                if (!field.isRequired()) {
                    yaml.append("          nullable: true\n");
                }
            }
        }
        
        return yaml.toString();
    }
    
    private String mapToJsonType(String javaType) {
        if (javaType.contains("String")) return "string";
        if (javaType.contains("Int") || javaType.contains("Long")) return "integer";
        if (javaType.contains("Double") || javaType.contains("Float") || javaType.contains("BigDecimal")) return "number";
        if (javaType.contains("Boolean")) return "boolean";
        if (javaType.contains("List") || javaType.contains("Array")) return "array";
        return "object";
    }
    
    private String escapeYaml(String text) {
        if (text == null) return "";
        return text.replace("\"", "'").replace("\n", " ").replace("\r", "");
    }
    
    private String convertYamlToJson(String yaml) {
        return yaml;
    }
    
    private String getSampleInvoiceController() {
        return """
            package com.example.billing;
            
            import org.springframework.web.bind.annotation.*;
            import java.util.List;
            
            @RestController
            @RequestMapping("/api/v1/invoices")
            public class InvoiceController {
                
                @PostMapping
                public Invoice createInvoice(@RequestBody InvoiceRequest request) {
                    return new Invoice();
                }
                
                @GetMapping("/{id}")
                public Invoice getInvoice(@PathVariable Long id) {
                    return new Invoice();
                }
                
                @PostMapping("/{id}/pdf")
                public void generatePdf(@PathVariable Long id) {
                }
                
                @GetMapping
                public List<Invoice> listInvoices() {
                    return List.of();
                }
            }
            """;
    }
    
    private String getSamplePaymentController() {
        return """
            package com.example.billing;
            
            import org.springframework.web.bind.annotation.*;
            import java.util.List;
            
            @RestController
            @RequestMapping("/api/v1/payments")
            public class PaymentController {
                
                @PostMapping
                public Payment processPayment(@RequestBody PaymentRequest request) {
                    return new Payment();
                }
                
                @GetMapping("/{id}")
                public Payment getPayment(@PathVariable Long id) {
                    return new Payment();
                }
                
                @PostMapping("/{id}/refund")
                public RefundResult refund(@PathVariable Long id) {
                    return new RefundResult();
                }
            }
            """;
    }
    
    private String getSampleCustomerDto() {
        return """
            package com.example.billing.dto;
            
            public class Invoice {
                public Long id;
                public String customerId;
                public Double amount;
                public String status;
                public String createdAt;
            }
            
            public class InvoiceRequest {
                public String customerId;
                public List<LineItem> items;
            }
            
            public class Payment {
                public Long id;
                public String transactionId;
                public Double amount;
            }
            
            public class LineItem {
                public String productId;
                public Integer quantity;
                public Double price;
            }
            """;
    }
}