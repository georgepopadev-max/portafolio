import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { GeneratedDoc, GenerationParams, GenerationStatus } from '../models/project.model';

@Injectable({ providedIn: 'root' })
export class GenerationService {
  
  private baseUrl = 'http://localhost:8080/api/generation';

  constructor(private http: HttpClient) {}

  startGeneration(projectId: string, params: GenerationParams): Observable<GeneratedDoc> {
    return this.http.post<GeneratedDoc>(`${this.baseUrl}/projects/${projectId}/generate`, params);
  }

  getStatus(projectId: string): Observable<GenerationStatus> {
    return this.http.get<GenerationStatus>(`${this.baseUrl}/projects/${projectId}/status`);
  }
}