import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class ExportService {
  
  private baseUrl = 'http://localhost:8080/api/export';

  constructor(private http: HttpClient) {}

  exportYaml(docId: string): Observable<string> {
    return this.http.get(`${this.baseUrl}/docs/${docId}/yaml`, { responseType: 'text' });
  }

  exportJson(docId: string): Observable<string> {
    return this.http.get(`${this.baseUrl}/docs/${docId}/json`, { responseType: 'text' });
  }

  updateSpec(docId: string, yaml: string): Observable<string> {
    return this.http.put<string>(`${this.baseUrl}/docs/${docId}`, yaml);
  }

  validateYaml(yaml: string): Observable<string> {
    return this.http.post<string>(`${this.baseUrl}/validate`, yaml);
  }

  downloadYaml(docId: string, filename: string): void {
    this.exportYaml(docId).subscribe(content => {
      this.downloadFile(content, filename, 'text/yaml');
    });
  }

  downloadJson(docId: string, filename: string): void {
    this.exportJson(docId).subscribe(content => {
      this.downloadFile(content, filename, 'application/json');
    });
  }

  private downloadFile(content: string, filename: string, contentType: string): void {
    const blob = new Blob([content], { type: contentType });
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = filename;
    a.click();
    window.URL.revokeObjectURL(url);
  }
}