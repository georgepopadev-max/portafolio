import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Project, GeneratedDoc, CreateProjectRequest, SourceUpload } from '../models/project.model';

@Injectable({ providedIn: 'root' })
export class ApiService {
  
  private baseUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}

  getProjects(): Observable<Project[]> {
    return this.http.get<Project[]>(`${this.baseUrl}/projects`);
  }

  getProject(id: string): Observable<Project> {
    return this.http.get<Project>(`${this.baseUrl}/projects/${id}`);
  }

  createProject(request: CreateProjectRequest): Observable<Project> {
    return this.http.post<Project>(`${this.baseUrl}/projects`, request);
  }

  deleteProject(id: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/projects/${id}`);
  }

  uploadSource(projectId: string, filename: string, fileSize: number, sourceCode: string): Observable<SourceUpload> {
    return this.http.post<SourceUpload>(`${this.baseUrl}/projects/${projectId}/upload`, {
      filename, fileSize, sourceCode
    });
  }

  getProjectUploads(projectId: string): Observable<SourceUpload[]> {
    return this.http.get<SourceUpload[]>(`${this.baseUrl}/projects/${projectId}/uploads`);
  }

  getProjectDocs(projectId: string): Observable<GeneratedDoc[]> {
    return this.http.get<GeneratedDoc[]>(`${this.baseUrl}/projects/${projectId}/docs`);
  }

  getLatestDoc(projectId: string): Observable<GeneratedDoc> {
    return this.http.get<GeneratedDoc>(`${this.baseUrl}/projects/${projectId}/docs/latest`);
  }
}