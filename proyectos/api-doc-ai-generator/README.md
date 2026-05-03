# 🤖 API Doc AI Generator

**Generador automático de documentación OpenAPI mediante IA**

[![Java](https://img.shields.io/badge/Java-21-blue.svg)](https://adoptium.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-green.svg)](https://spring.io/projects/spring-boot)
[![Angular](https://img.shields.io/badge/Angular-17-red.svg)](https://angular.io/)
[![AI](https://img.shields.io/badge/AI-GPT--4-orange.svg)](https://openai.com/)

---

## 📖 Descripción

Herramienta que analiza código Java (especificamente proyectos Spring Boot) y genera automáticamente documentación OpenAPI 3.0 (Swagger) utilizando inteligencia artificial. El flujo va desde la subida del código fuente hasta la revisión y edición del YAML generado.

**Problema que resuelve:** Mantener la documentación de APIs REST actualizada es una tarea que se néglige constantemente. Este generador automatiza el análisis de anotaciones Spring (`@RestController`, `@RequestMapping`, `@Operation`, etc.) y usa IA para enriquecer las descripciones con contexto semántico que un parser puramente sintáctico no puede inferir.

---

## 🧰 Tech Stack

| Capa | Tecnología |
|---|---|
| **Frontend** | Angular 17 · TypeScript |
| **Backend** | Spring Boot 3.2 · Java 21 |
| **Parser** | JavaParser (AST analysis) |
| **IA** | OpenAI GPT-4 · GPT4All (local, opcional) |
| **Output** | OpenAPI 3.0 / Swagger YAML |
| **UI** | Editor Monaco (YAML) |

---

## ✨ Features

- **Upload de código fuente:** Arrastra o selecciona archivos Java/.zip con código Spring Boot.
- **Parsing de anotaciones Spring:** Extrae endpoints, DTOs, parámetros y respuestas usando JavaParser AST.
- **Generación con IA:** GPT-4 (o GPT4All local) genera descripciones enriched para cada endpoint y DTO.
- **Editor YAML integrado:** Monaco Editor con sintaxis YAML y preview Swagger en tiempo real.
- **Histórico de versiones:** Cada generación se guarda con timestamp y diff frente a la versión anterior.

---

## 🎨 Demo

El flujo de uso es:

1. **Wizard de creación** — Paso 1: upload del código. Paso 2: revisión de endpoints detectados. Paso 3: configuración del modelo de IA.
2. **Progreso de generación** — Barra de progreso con logs en tiempo real: parsing → análisis → prompt → generación → merge.
3. **Editor de documentación** — Split view: editor YAML a la izquierda, preview Swagger a la derecha. Permite editar y volver a generar endpoints específicos.
4. **Exportar / Guardar** — Descarga del YAML o guardado en el histórico del proyecto.

---

## 🏗️ Arquitectura

```
┌──────────────────────────────────────────────────────────┐
│               Frontend (Angular 17)                      │
│   UploadComponent · WizardComponent · MonacoEditor       │
└────────────────────────┬────────────────────────────────┘
                         │ HTTP REST
┌────────────────────────▼────────────────────────────────┐
│                Backend (Spring Boot)                      │
│    CodeAnalysisController · OpenAPIGeneratorService       │
└────────────────────────┬────────────────────────────────┘
                         │
┌────────────────────────▼────────────────────────────────┐
│              AI Integration Layer                        │
│   OpenAIGateway · GPT4AllGateway · PromptBuilder        │
└──────────────────────────────────────────────────────────┘
```

---

## 🚀 Setup

### Requisitos

- **Java 21**
- **Node.js 18+** y **npm**
- **Angular CLI 17**
- **Clave API de OpenAI** (solo si se usa GPT-4 cloud; GPT4All es opcional local)

### Backend

```bash
cd api-doc-ai-generator/backend
./mvnw spring-boot:run
# API disponible en http://localhost:8080
```

### Frontend

```bash
cd api-doc-ai-generator/frontend
npm install
ng serve
# App disponible en http://localhost:4200
```

### Configuración de la API Key de OpenAI

```bash
# Variable de entorno (Linux/macOS)
export OPENAI_API_KEY=sk-tu-clave-aqui

# O configurar en backend/src/main/resources/application.yml
# openai:
#   api-key: sk-tu-clave-aqui
```

### Uso con GPT4All (local, sin API key)

```yaml
# En application.yml del backend
ai:
  provider: gpt4all  # Cambiar de "openai" a "gpt4all"
  gpt4all:
    model: nous-hermes-llama3-8b
```

---

## 📂 Estructura del proyecto

```
api-doc-ai-generator/
├── backend/
│   ├── src/main/java/.../
│   │   ├── controller/     # CodeAnalysisController
│   │   ├── service/        # OpenAPIGeneratorService
│   │   ├── parser/         # SpringAnnotationParser (JavaParser)
│   │   ├── ai/             # OpenAIGateway, GPT4AllGateway
│   │   └── model/          # DetectedEndpoint, ApiSpec
│   └── src/main/resources/
└── frontend/
    ├── src/app/
    │   ├── upload/         # Componente de upload
    │   ├── wizard/         # Wizard de generación
    │   ├── editor/         # Monaco YAML editor
    │   └── history/        # Histórico de versiones
    └── src/assets/
```

---

## 📬 Contacto

- ✉️ **Email:** [georgepopadev@gmail.com](mailto:georgepopadev@gmail.com)
- 💻 **GitHub:** [github.com/georgepopadev/api-doc-ai-generator](https://github.com/georgepopadev/api-doc-ai-generator)
