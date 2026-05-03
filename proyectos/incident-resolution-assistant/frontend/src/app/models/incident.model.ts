export interface ChatMessage {
  id: string;
  role: 'USER' | 'ASSISTANT' | 'SYSTEM';
  content: string;
  citations: string;
  createdAt: Date;
}

export interface Thread {
  id: string;
  title: string;
  status: 'ACTIVE' | 'RESOLVED' | 'ESCALATED';
  category: string;
  createdAt: Date;
  resolvedAt: Date | null;
}

export interface Citation {
  documentId: string;
  title: string;
  sourceType: string;
  relevanceScore: number;
  snippet: string;
}

export interface ChatRequest {
  threadId: string | null;
  message: string;
}

export interface ChatResponse {
  message: string;
  citations: Citation[];
  threadId: string;
}

export interface ResolutionRequest {
  threadId: string;
  summary: string;
  steps: string;
  outcome: string;
  jiraTicketId: string;
  resolvedBy: string;
}

export interface Resolution {
  id: string;
  threadId: string;
  summary: string;
  steps: string;
  jiraTicketId: string;
  resolvedBy: string;
  resolvedAt: Date;
}
