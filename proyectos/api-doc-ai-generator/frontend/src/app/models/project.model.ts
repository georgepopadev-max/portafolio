export interface Project {
  id: string;
  name: string;
  description: string;
  endpointCount: number;
  status: string;
  lastGenerated: string | null;
  aiProvider: string;
  uploads?: SourceUpload[];
}

export interface SourceUpload {
  id: string;
  filename: string;
  fileSize: number;
  uploadedAt: string;
  controllerCount: number;
  endpointCount: number;
}

export interface GeneratedDoc {
  id: string;
  projectId: string;
  version: number;
  specYaml: string;
  generatedAt: string;
  modelUsed: string;
  status: string;
}

export interface GenerationParams {
  temperature: number;
  maxTokens: number;
  detailLevel: string;
  model: string;
}

export interface GenerationStatus {
  status: string;
  currentStep: string;
  stepNumber: number;
  totalSteps: number;
  aiRequestsProcessed: number;
  message: string;
}

export interface CreateProjectRequest {
  name: string;
  description: string;
  aiProvider: string;
}