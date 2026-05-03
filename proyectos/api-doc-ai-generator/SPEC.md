# API Doc AI Generator — Project Specification

## 1. Project Overview

**Project Name:** API Doc AI Generator  
**Type:** AI-powered documentation generation platform  
**Core Functionality:** An intelligent system that analyzes Java source code and automatically generates comprehensive Swagger/OpenAPI documentation, including endpoint descriptions, request/response schemas, authentication requirements, and example payloads.  
**Target Users:** Backend developers, API architects, technical writers, and development teams needing to maintain accurate API documentation.

---

## 2. Description

API Doc AI Generator is a specialized tool that leverages large language models to understand Java source code structure and generate accurate, detailed OpenAPI 3.0 specifications. The system parses Java classes — including Spring controllers, DTOs, entities, and service methods — and produces swagger-ready documentation with intelligent descriptions, parameter explanations, and realistic example values.

The generator supports both OpenAI's GPT models and locally-hosted alternatives like GPT4All, giving teams the flexibility to use cloud-based or on-premise AI processing. The Angular frontend provides a project-based workflow where developers upload or link their repositories, configure generation parameters, and review/edit the generated documentation before exporting.

Key capabilities include understanding Spring annotations (@RestController, @RequestMapping, @Valid), inferring data types from Java classes, mapping JPA entities to schema definitions, and generating field-level descriptions based on naming conventions and comments. The system learns from user corrections to improve future generations.

---

## 3. Technology Stack

### Frontend
- **Framework:** Angular 17 (standalone components)
- **Code Editor:** Monaco Editor for previewing Java source alongside generated docs
- **UI Library:** Angular Material with clean documentation-theme styling
- **File Upload:** Angular file upload with drag-and-drop, supports .zip and .tar.gz archives
- **State Management:** NgRx for project state, RxJS for async operations
- **Build Tool:** Angular CLI

### Backend
- **Framework:** Spring Boot 3.2 (Java 17+)
- **AI Integration:** OpenAI GPT-4 API client + GPT4All Java bindings
- **Code Parsing:** JavaParser library for AST analysis
- **Template Engine:** Handlebars for custom documentation templates
- **Database:** PostgreSQL 15 for project metadata, generated docs, and version history
- **File Storage:** Local filesystem with configurable base path for uploaded source archives
- **Authentication:** JWT with GitHub OAuth for repository linking

### Infrastructure
- **Containerization:** Docker & Docker Compose
- **AI Models:** Support for GPT-4, GPT-3.5-turbo, and local Llama2 variants via GPT4All

---

## 4. Feature List

### Core Features
1. **Source Code Ingestion** — Upload ZIP archives, link GitHub repositories, or provide direct file paths
2. **Project Management** — Create, manage, and version-control documentation projects
3. **AI-Powered Generation** — Analyze Java code and generate OpenAPI 3.0 specs with descriptions
4. **Supported Annotations** — Full support for Spring (@RestController, @GetMapping, @PostMapping, etc.)
5. **Schema Generation** — Automatic DTO/entity to schema conversion with nested object support
6. **Authentication Documentation** — Detect security configurations (Spring Security, JWT, API keys)
7. **Example Payload Generation** — Produce realistic JSON examples based on field names and types
8. **Documentation Editor** — Review and manually edit generated specs with live preview
9. **Version Comparison** — Diff view showing changes between documentation versions
10. **Export Options** — Download as YAML/JSON, integrate with existing API gateways

### AI Features
- Configurable AI model selection (OpenAI or local)
- Adjustable generation detail level (brief/standard/detailed)
- Batch processing for large codebases
- Rate limiting and cost tracking for OpenAI usage
- Prompt customization for organization-specific terminology

### Collaboration Features
- Team workspaces with role-based permissions
- Generation history with rollback capability
- Comment threads on documentation sections
- Integration with CI/CD pipelines via REST API

---

## 5. Architecture

### High-Level Architecture

```
┌─────────────────────────────────────────────────────────────────────┐
│                       Angular Frontend                              │
│   Project Dashboard │ Code Viewer │ Doc Editor │ Settings │ Export  │
└──────────────────────────────┬──────────────────────────────────────┘
                               │ REST API
┌──────────────────────────────▼──────────────────────────────────────┐
│                      Spring Boot API                                 │
│   Project Service │ Generation Orchestrator │ Auth Service          │
└────────┬─────────────────────┬──────────────────────┬────────────────┘
         │                     │                      │
  ┌──────▼──────┐  ┌───────────▼───────────┐  ┌──────▼──────┐
  │ Code Parser │  │ AI Service (OpenAI/     │  │ Doc Storage │
  │ (JavaParser)│  │ GPT4All)               │  │ (PostgreSQL)│
  └─────────────┘  └────────────────────────┘  └─────────────┘
                         │
              ┌──────────▼──────────┐
              │ File Storage        │
              │ (Source Archives)   │
              └─────────────────────┘
```

### Frontend Modules

**`app/features/projects/`** — Project list, creation wizard, settings, team management  
**`app/features/code-viewer/`** — Monaco editor for browsing uploaded source code  
**`app/features/doc-editor/`** — Generated OpenAPI spec editor with YAML/JSON toggle and live validation  
**`app/features/generation/`** — Generation progress view, model selection, parameter configuration  
**`app/shared/components/`** — File upload zone, diff viewer, version timeline

### Backend Modules

**`parser/`** — JavaParser-based source code analysis, extracts controllers, endpoints, DTOs, entities  
**`generator/`** — Orchestrates AI requests, constructs prompts from parsed code, handles streaming responses  
**`ai.client/`** — Pluggable AI provider interface (OpenAI implementation, GPT4All implementation)  
**`exporter/`** — OpenAPI spec serialization, Swagger UI bundling, download generation  
**`versioning/`** — Diff computation between spec versions using OpenAPI comparator

### Data Model

**Project**
- id (UUID), name, description, createdAt, updatedAt, ownerId, aiProvider (OPENAI/GPT4ALL)

**SourceUpload**
- id (UUID), projectId (FK), filename, uploadedAt, filePath, fileSize

**GeneratedDoc**
- id (UUID), projectId (FK), version, specYaml, specJson, generatedAt, modelUsed, generationParams

**GenerationRequest**
- id (UUID), projectId, status (PENDING/PROCESSING/COMPLETED/FAILED), progress, startedAt, completedAt, errorMessage

---

## 6. Deliverables

1. **Source Code** — Complete Angular frontend and Spring Boot backend
2. **Docker Compose** — Full environment including PostgreSQL, with optional GPT4All container
3. **AI Provider Integrations** — OpenAI GPT-4 client and GPT4All Llama2 integration
4. **JavaParser Integration** — Robust parsing of Spring Boot applications
5. **API Documentation** — OpenAPI spec for the generator's own REST API
6. **Test Suite** — Unit tests for code parsing, integration tests for AI service, E2E for generation flow
7. **README** — Setup guide, AI provider configuration, usage examples, CI/CD integration

---

## 7. Demo Description

The demo presents a browser-based workflow for generating API documentation.

**Project Dashboard:** The landing page shows a list of projects including "Energy API v2" and "Grid Monitor Service." Each project card shows last generation date, endpoint count, and health status indicator.

**New Project Wizard:** Clicking "New Project" opens a stepped wizard:
1. **Name & Description** — Project name "Billing Service API", brief description
2. **Source Selection** — Drag-and-drop zone accepts a ZIP file labeled "billing-service-src.zip" (1.2 MB). Alternatively, connect GitHub repository by pasting URL.
3. **AI Configuration** — Dropdown selects "OpenAI GPT-4" or "Local Llama2 (GPT4All)". Advanced settings expandable: temperature (0.7), max tokens (2000), detail level dropdown (Brief/Standard/Detailed)
4. **Generation Scope** — Checkbox tree showing discovered controllers: ☑ InvoiceController, ☑ PaymentController, ☑ CustomerController, with endpoint counts per controller

**Generation Progress:** After clicking "Generate Documentation," a progress screen appears:
- Scanning source files... (step 1/4) with animated progress bar
- Analyzing Spring annotations... (step 2/4)
- Generating endpoint descriptions... (step 3/4) — "AI processing: 47 requests"
- Finalizing OpenAPI spec... (step 4/4)

**Documentation Editor:** The generated spec opens in a split view:
- Left panel: Generated OpenAPI YAML with endpoint descriptions like:
  ```yaml
  /api/v1/invoices/{id}/pdf:
    post:
      summary: "Generate PDF for an invoice"
      description: "Triggers async PDF generation for the specified invoice. 
        Uses invoice-template-v2 and includes QR code for payment."
  ```
- Right panel: Live preview rendered as an interactive API explorer with "Try it out" functionality

**Edit & Refine:** Clicking any endpoint description opens an inline edit mode. The AI-generated text can be refined, and the change is marked as "manual override" to inform future retraining.

**Export Options:** Top toolbar has buttons for "Download YAML", "Download JSON", "Open in Swagger UI", and "Copy API Endpoint URL."

**Version History:** A timeline sidebar shows 5 previous generations with timestamps. Selecting an older version shows a diff view highlighting additions (green) and deletions (red) compared to current.