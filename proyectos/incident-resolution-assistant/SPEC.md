# Incident Resolution Assistant — Project Specification

## 1. Project Overview

**Project Name:** Incident Resolution Assistant  
**Type:** RAG-powered chatbot for technical incident resolution  
**Core Functionality:** An intelligent assistant that helps operations teams resolve technical incidents faster by retrieving relevant documentation, past incident histories, runbooks, and knowledge base articles using Retrieval-Augmented Generation (RAG).  
**Target Users:** DevOps engineers, SREs, support engineers, and development teams managing production incidents.

---

## 2. Description

Incident Resolution Assistant is a specialized RAG chatbot designed for technical operations environments. When an incident occurs, engineers often waste critical time searching through scattered documentation, Slack messages, and wikis. This system centralizes knowledge retrieval by indexing documentation from Confluence, GitHub, runbook repositories, and past incident records into a vector database.

When an engineer describes an incident or pastes an error message, the system retrieves the most relevant context — similar past incidents with resolutions, relevant runbook sections, architecture diagrams, and dependency information — and presents this alongside an AI-generated summary and suggested resolution steps. The LLM (configurable between OpenAI GPT-4 and local alternatives) generates responses grounded in the retrieved documents, reducing hallucination risk.

The Spring Boot backend orchestrates the RAG pipeline: document ingestion, chunking, embedding generation, vector storage (using pgvector for PostgreSQL), similarity search, and response generation. The Angular frontend provides a conversational interface optimized for high-stress incident scenarios — minimal clicks, instant results, clear citations.

---

## 3. Technology Stack

### Frontend
- **Framework:** Angular 17 (standalone components)
- **Chat Interface:** Custom chat component with streaming response display
- **Message Types:** Text, code blocks, citation cards, action buttons
- **UI Library:** Angular Material with dark-mode optimized theme (suitable for NOC environments)
- **Context Features:** Thread management, incident tagging, resolution marking
- **State Management:** NgRx for conversation state, RxJS for streaming
- **Build Tool:** Angular CLI

### Backend
- **Framework:** Spring Boot 3.2 (Java 17+)
- **RAG Pipeline:** Custom implementation with document processing, embedding, retrieval, generation
- **Vector Database:** pgvector extension for PostgreSQL 15
- **Embedding Service:** OpenAI embeddings API (text-embedding-ada-002) or local sentence transformers
- **LLM Integration:** OpenAI GPT-4 API client + local model fallback (Ollama)
- **Document Sources:** Confluence REST API, GitHub API, file system ingestion
- **Authentication:** JWT with SSO integration support

### Infrastructure
- **Containerization:** Docker & Docker Compose
- **Database:** PostgreSQL 15 with pgvector extension

---

## 4. Feature List

### Core Features
1. **Conversational Interface** — Chat UI where engineers describe incidents and receive contextual responses
2. **RAG Retrieval** — Similarity search across indexed documents using vector embeddings
3. **Streaming Responses** — Real-time token streaming from LLM with citation updates
4. **Source Citation** — Each response cites specific documents with relevance scores, clickable to view source
5. **Incident Thread History** — Persist conversation threads with resolution status and outcome tracking
6. **Multi-Source Knowledge Base** — Index from Confluence, GitHub wikis, runbook repos, Slack exports
7. **Similar Incident Search** — Find past incidents with similar error patterns or symptoms
8. **Resolution Suggestions** — AI-generated step-by-step resolution based on retrieved context
9. **Quick Actions** — One-click buttons: "Mark Resolved," "Create Jira Ticket," "Page On-Call"
10. **Incident Categorization** — Auto-tag incidents by type (database, network, application, infrastructure)

### RAG Features
- **Document Chunking** — Configurable chunk size (512-2048 tokens) with overlap
- **Embedding Models** — Support for OpenAI ada-002 and local sentence-transformers
- **Hybrid Search** — Combine vector similarity with keyword-based filtering
- **Re-ranking** — Cross-encoder re-ranking for improved result quality
- **Metadata Filtering** — Filter by document source, date, incident type, affected service
- **Incremental Indexing** — Real-time updates when new documents are added

### Integration Features
- **Confluence Integration** — OAuth-based connection, automatic page indexing
- **GitHub Integration** — Index README files, wiki pages, and repository documentation
- **Jira Integration** — Create issues from incidents, link resolutions to tickets
- **PagerDuty Webhook** — Receive alerts and auto-create incident threads
- **Webhook Outgoing** — Send resolved incidents to external systems

---

## 5. Architecture

### High-Level Architecture

```
┌─────────────────────────────────────────────────────────────────────┐
│                         Angular Frontend                            │
│   Chat Interface │ Thread History │ Source Viewer │ Quick Actions   │
└──────────────────────────────┬──────────────────────────────────────┘
                               │ REST + Streaming
┌──────────────────────────────▼──────────────────────────────────────┐
│                       Spring Boot API                                │
│   Chat Service │ RAG Orchestrator │ LLM Client │ Auth Service         │
└────────┬─────────────────────┬──────────────────────┬────────────────┘
         │                     │                      │
  ┌──────▼──────┐  ┌───────────▼───────────┐  ┌──────▼──────┐
  │ Embedding   │  │ Vector Store (pgvector)│  │ Document    │
  │ Service     │  │ + PostgreSQL             │  │ Sources     │
  └─────────────┘  └─────────────────────────┘  └─────────────┘
         │                     │                      │
  ┌──────▼─────────────────────▼──────────────────────▼──────┐
  │            External Integrations                         │
  │  Confluence │ GitHub │ Jira │ PagerDuty                 │
  └───────────────────────────────────────────────────────────┘
```

### Frontend Modules

**`app/features/chat/`** — Main chat interface with streaming display, input area, attachment support  
**`app/features/threads/`** — Incident thread list, search, filter by status/category  
**`app/features/sources/`** — Source document viewer with highlighted relevant sections  
**`app/features/incidents/`** — Incident detail view, resolution form, metadata editor  
**`app/shared/components/`** — Citation card, code block renderer, relevance badge, action button

### Backend Modules

**`rag/`** — Retrieval pipeline: query embedding → vector search → context assembly → prompt construction  
**`llm/`** — LLM client interface with OpenAI and Ollama implementations, streaming support  
**`embedding/`** — Embedding generation service with batching and caching  
**`indexing/`** — Document ingestion pipeline: fetch → parse → chunk → embed → store  
**`sources/`** — Source connectors for Confluence, GitHub, file system, Jira  
**`chat/`** — Conversation management, thread persistence, message history  
**`analytics/`** — Usage metrics, resolution time tracking, popular resolutions

### Data Model

**Document**
- id (UUID), sourceType, sourceId, title, content, embedding (vector), metadata (JSON), indexedAt

**DocumentChunk**
- id (UUID), documentId (FK), chunkText, position, embedding (vector), relevanceScore

**IncidentThread**
- id (UUID), title, status (ACTIVE/RESOLVED/ESCALATED), category, createdAt, resolvedAt, outcome

**ChatMessage**
- id (UUID), threadId (FK), role (USER/ASSISTANT/SYSTEM), content, citations (JSON), createdAt

**Resolution**
- id (UUID), threadId (FK), summary, steps (text), jiraTicketId, resolvedBy, resolvedAt

---

## 6. Deliverables

1. **Source Code** — Complete Angular frontend and Spring Boot backend
2. **Docker Compose** — Full environment with PostgreSQL + pgvector extension
3. **RAG Pipeline** — Document ingestion, chunking, embedding, and retrieval implementation
4. **LLM Integrations** — OpenAI GPT-4 and Ollama local model support
5. **Source Connectors** — Confluence, GitHub, file system ingestion implementations
6. **API Documentation** — OpenAPI spec for REST endpoints and WebSocket streaming
7. **Test Suite** — RAG retrieval accuracy tests, LLM response quality evaluation, integration tests
8. **README** — Setup, source configuration, embedding model selection, first incident walkthrough

---

## 7. Demo Description

The demo presents the Incident Resolution Assistant in a dark-themed interface suitable for Network Operations Centers.

**Chat Interface:** The main screen shows a chat conversation:

**User**: "Getting大量 504 gateway timeout errors on the billing-api service since 18:30"

**Assistant** (streaming): "Based on my检索, I找到 3个相关 incident记录和 5份 relevant documentation:

**[相似Incident]** `2024-11-15` — "Billing API 504 errors during payment batch" — Resolved in 47 min — Solution: Restarted billing-service pods after memory leak in invoice processor.

**[Runbook]** `database/runbooks/billing-service.md` — Section "504 Timeout Troubleshooting" — Step 3 mentions checking connection pool exhaustion.

**[Documentation]** `architecture/billing-service.md` — "The billing service connects to PostgreSQL via HikariCP with max pool size 20. Under high load, connections can exhaust..."

**[Confluence]** `wiki/Incident-Playbook` — "504 errors typically indicate upstream timeout, check: 1) Database connection pool 2) External API timeouts 3) Memory pressure"

**Suggested Actions:**
- [View billing-service connection pool metrics]
- [Check PostgreSQL active connections]
- [Escalate to DBA team]

**Citation Cards:** Below the response, 4 citation cards are shown:
1. "Past Incident #4582" — 94% relevance — Click to expand
2. "Runbook: Billing Troubleshooting" — 89% relevance — Click to open
3. "Architecture Doc: Billing Service" — 85% relevance — Click to open
4. "Incident Playbook" — 78% relevance — Click to open

**Quick Actions Bar:** Below the input area:
- "Mark Resolved" (green) — Opens resolution form
- "Create Jira" (blue) — Opens ticket creation with pre-filled description
- "Page On-Call" (red) — Sends PagerDuty alert

**Thread Sidebar:** Left sidebar shows incident threads:
- "504 errors on billing-api" (ACTIVE, 12 min ago) ← current
- "DB connection exhaustion" (RESOLVED, 2 hours ago)
- "Memory leak in grid-processor" (RESOLVED, 1 day ago)

**Source Configuration:** Settings page shows configured knowledge sources:
- Confluence: "TechDocs" workspace, 1,247 pages indexed
- GitHub: "energy-platform" org, 8 repos indexed
- Runbooks: `/docs/runbooks` folder, 45 documents indexed

**Index Status:** Shows last indexing time and document counts by source. "Re-index Confluence" button for manual refresh.

**Resolution Form:** When "Mark Resolved" is clicked, a modal appears with:
- Summary field (pre-filled from chat)
- Resolution steps textarea
- Outcome dropdown: (RESOLVED / WORKAROUND_APPLIED / ESCALATED)
- "Link Jira Ticket" field (optional)
- Submit creates resolution and closes thread

**Streaming Toggle:** A setting allows switching between streaming mode (token-by-token) and complete response mode for environments with unstable connections.