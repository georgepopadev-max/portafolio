import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ChatMessage, ChatRequest, ChatResponse, Thread, ResolutionRequest, Resolution } from '../models/incident.model';

@Injectable({ providedIn: 'root' })
export class IncidentService {
  private http = inject(HttpClient);
  private baseUrl = '/api';

  sendMessage(request: ChatRequest): Observable<ChatResponse> {
    return this.http.post<ChatResponse>(`${this.baseUrl}/chat`, request);
  }

  getThreads(): Observable<Thread[]> {
    return this.http.get<Thread[]>(`${this.baseUrl}/threads`);
  }

  getThreadMessages(threadId: string): Observable<ChatMessage[]> {
    return this.http.get<ChatMessage[]>(`${this.baseUrl}/threads/${threadId}/messages`);
  }

  createResolution(request: ResolutionRequest): Observable<Resolution> {
    return this.http.post<Resolution>(`${this.baseUrl}/resolutions`, request);
  }

  getResolution(threadId: string): Observable<Resolution> {
    return this.http.get<Resolution>(`${this.baseUrl}/threads/${threadId}/resolution`);
  }

  createJiraTicket(ticketData: { summary: string; description: string; threadId: string }): Observable<{ ticketId: string; status: string; message: string }> {
    return this.http.post<{ ticketId: string; status: string; message: string }>(`${this.baseUrl}/jira/ticket`, ticketData);
  }
}
