# 🆘 Incident Resolution Assistant

**Asistente RAG para resolución de incidencias técnicas en entornos de producción**

[![Java](https://img.shields.io/badge/Java-21-blue.svg)](https://adoptium.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-green.svg)](https://spring.io/projects/spring-boot)
[![Angular](https://img.shields.io/badge/Angular-17-red.svg)](https://angular.io/)

---

## 📖 Descripción

Chatbot conversacional que recupera documentación técnica relevante para resolver incidencias en producción utilizando un pipeline RAG (Retrieval-Augmented Generation). El sistema indexa documentos internos (runbooks, manuales de ops, FAQs de incidencias) y responde a preguntas del operador con citations precisas a los documentos de origen.

**Problema que resuelve:** En entornos de producción, cuando salta una incidencia, el operador necesita encontrar la documentación correcta rápidamente. Buscar en Confluence/Notion/wiki entre cientos de documentos consume tiempo valioso. Este asistente reduce ese tiempo recuperando el documento exacto con context del incidente.

---

## 🧰 Tech Stack

| Capa | Tecnología |
|---|---|
| **Frontend** | Angular 17 · TypeScript |
| **Backend** | Spring Boot 3.2 · Java 21 |
| **Búsqueda** | TF-IDF · Cosine similarity (Spring ML) |
| **IA** | Mock LLM (desarrollo) · OpenAI GPT-4 (producción) |
| **Indexación** | Chunking semántico · Embeddings (TF-IDF vectors) |
| **UI** | Chat interface · Citation cards · Thread history |

---

## ✨ Features

- **Interfaz de chat conversacional:** Input de pregunta con respuestas en estilo chatbot. Historial de la conversación preservado por sesión.
- **Retrieval RAG:** El sistema recupera los 5 fragmentos más relevantes de la knowledge base y los incluye en el prompt del LLM.
- **Citation cards:** Cada respuesta incluye tarjetas de citation numeradas con snippet del documento origen, enlace y score de relevancia.
- **Historial de threads:** Guardado de conversaciones por incidente con opción de retomar una sesión previa.
- **Formulario de resolución:** Al finalizar, el operador registra la incidencia como resuelta con resumen de la solución para alimentar la base de conocimiento.
- **Quick actions:** Botones de acceso rápido a las incidencias más comunes (alta CPU, OOM, network timeout, etc.).

---

## 🎨 Demo

La interfaz muestra:

1. **Chat principal** — Burbujas de mensaje con timestamp. El bot responde con texto + citation cards debajo.
2. **Citation card** — Pega el fragmento recuperado, indica la fuente (nombre del documento), relevancia (score 0-1) y un botón para abrir el documento completo.
3. **Sidebar de incidentes** — Lista de incidentes recientes con estado (🟡 Abierta / 🟢 Resuelta) y título.
4. **Panel de quick actions** — Botones preconfigurados para queries comunes del sector energético.

---

## 🏗️ Arquitectura

```
                      ┌─────────────────────────────────┐
                      │       Knowledge Base             │
                      │  (Runbooks · FAQs · Manuales)      │
                      └──────────────┬──────────────────┘
                                     │ Chunking + Indexing
                      ┌──────────────▼──────────────────┐
                      │      Embedding Layer             │
                      │   TF-IDF Vectorizer              │
                      └──────────────┬──────────────────┘
                                     │ Query
┌──────────────────┐    Query        │
│  User Question   │ ────────────────▶│
└──────────────────┘                 │
                                     ▼
                      ┌─────────────────────────────────┐
                      │    Retrieval (Top-K Chunks)       │
                      │   Cosine Similarity Search        │
                      └──────────────┬──────────────────┘
                                     │ Retrieved Context
                                     ▼
                      ┌─────────────────────────────────┐
                      │         LLM (GPT-4 / Mock)        │
                      │   Augmented Prompt + Context     │
                      └──────────────┬──────────────────┘
                                     │ Response + Citations
                                     ▼
                      ┌─────────────────────────────────┐
                      │      Frontend (Angular 17)        │
                      │   ChatInterface · CitationCard   │
                      └─────────────────────────────────┘
```

---

## 🚀 Setup

### Requisitos

- **Java 21**
- **Node.js 18+** y **npm**
- **Angular CLI 17**

### Knowledge base pre-cargada

El proyecto incluye una knowledge base de ejemplo con documentos del sector energético. Para producción, se pueden indexar documentos reales.

```bash
# Los documentos de ejemplo están en:
backend/src/main/resources/knowledge-base/
```

### Backend

```bash
cd incident-resolution-assistant/backend
./mvnw spring-boot:run
# API disponible en http://localhost:8080
```

### Frontend

```bash
cd incident-resolution-assistant/frontend
npm install
ng serve
# App disponible en http://localhost:4200
```

### Activar OpenAI GPT-4 (opcional)

```bash
# Variable de entorno
export OPENAI_API_KEY=sk-tu-clave-aqui

# En application.yml
# llm:
#   provider: openai
#   openai:
#     api-key: sk-tu-clave-aqui
#     model: gpt-4

# Por defecto usa MockLLM (respuestas predefinidas, sin API key)
```

---

## 📂 Estructura del proyecto

```
incident-resolution-assistant/
├── backend/
│   ├── src/main/java/.../
│   │   ├── controller/     # ChatController · IncidentController
│   │   ├── service/         # RAGService · RetrievalService
│   │   ├── embedding/       # TfidfVectorizer
│   │   ├── llm/             # LLMGateway · MockLLM · OpenAILLM
│   │   ├── model/           # ChatMessage, Incident, Citation
│   │   └── resources/
│   │       └── knowledge-base/ # Documentos de ejemplo
│   └── src/main/resources/
└── frontend/
    ├── src/app/
    │   ├── chat/            # Interfaz de chat
    │   ├── citation-card/   # Tarjetas de citation
    │   ├── incident-sidebar/# Sidebar de incidentes
    │   └── quick-actions/   # Botones de acción rápida
    └── src/assets/
```

---

## 📬 Contacto

- ✉️ **Email:** [georgepopadev@gmail.com](mailto:georgepopadev@gmail.com)
- 💻 **GitHub:** [github.com/georgepopadev/incident-resolution-assistant](https://github.com/georgepopadev/incident-resolution-assistant)
