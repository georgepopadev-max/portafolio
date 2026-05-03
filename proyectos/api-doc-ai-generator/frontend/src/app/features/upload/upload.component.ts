import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../services/api.service';
import { SourceUpload } from '../../models/project.model';

@Component({
  selector: 'app-upload',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="upload-container">
      <button class="back-btn" (click)="goBack()">
        <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
          <path d="M10 12L6 8l4-4" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
        Back to Dashboard
      </button>

      <h1>Upload Source Code</h1>
      <p class="subtitle">Upload Java source files or paste code directly to generate API documentation</p>

      <div class="upload-section">
        <div class="file-upload-zone" 
             [class.dragging]="isDragging"
             (dragover)="onDragOver($event)"
             (dragleave)="onDragLeave($event)"
             (drop)="onDrop($event)">
          <svg width="48" height="48" viewBox="0 0 48 48" fill="none">
            <rect x="8" y="8" width="32" height="32" rx="4" stroke="#6366f1" stroke-width="2"/>
            <path d="M24 32V16M24 16l-6 6M24 16l6 6" stroke="#6366f1" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
          <p class="upload-text">Drag & drop source files here</p>
          <p class="upload-hint">Supports .java files, .zip archives</p>
          <input type="file" (change)="onFileSelected($event)" accept=".java,.zip" hidden #fileInput>
          <button class="btn btn-primary" (click)="fileInput.click()">Browse Files</button>
        </div>

        <div class="divider">
          <span>OR</span>
        </div>

        <div class="paste-section">
          <label>Paste Java Code</label>
          <textarea [(ngModel)]="pastedCode" placeholder="Paste your Java controller code here..."
                    rows="12"></textarea>
          @if (pastedCode.length > 0) {
            <p class="code-preview">{{ pastedCode.split('\n').length }} lines of code</p>
          }
        </div>
      </div>

      @if (selectedFile) {
        <div class="file-info">
          <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
            <path d="M4 4h8l4 4v8a2 2 0 01-2 2H4a2 2 0 01-2-2V4z" stroke="#6366f1" stroke-width="1.5"/>
            <path d="M12 4v4h4" stroke="#6366f1" stroke-width="1.5"/>
          </svg>
          <span>{{ selectedFile.name }}</span>
          <span class="file-size">{{ formatFileSize(selectedFile.size) }}</span>
        </div>
      }

      <button class="btn btn-primary btn-lg" (click)="upload()" [disabled]="!pastedCode && !selectedFile">
        Upload and Parse
      </button>

      @if (uploads.length > 0) {
        <div class="existing-uploads">
          <h3>Previously Uploaded</h3>
          @for (upload of uploads; track upload.id) {
            <div class="upload-item">
              <div class="upload-info">
                <span class="upload-name">{{ upload.filename }}</span>
                <span class="upload-meta">{{ upload.controllerCount }} controllers, {{ upload.endpointCount }} endpoints</span>
              </div>
              <span class="upload-date">{{ formatDate(upload.uploadedAt) }}</span>
            </div>
          }
        </div>
      }
    </div>
  `,
  styles: [`
    .upload-container { max-width: 800px; margin: 0 auto; }
    .back-btn {
      display: inline-flex;
      align-items: center;
      gap: 6px;
      color: #64748b;
      background: none;
      border: none;
      cursor: pointer;
      font-size: 14px;
      margin-bottom: 24px;
    }
    .back-btn:hover { color: #6366f1; }
    h1 { font-size: 28px; font-weight: 700; color: #1e293b; margin: 0 0 8px 0; }
    .subtitle { color: #64748b; margin: 0 0 32px 0; }
    .upload-section { display: flex; flex-direction: column; gap: 24px; }
    .file-upload-zone {
      border: 2px dashed #e2e8f0;
      border-radius: 12px;
      padding: 48px;
      text-align: center;
      transition: all 0.2s;
      background: white;
    }
    .file-upload-zone.dragging { border-color: #6366f1; background: #faf5ff; }
    .file-upload-zone svg { margin-bottom: 16px; }
    .upload-text { font-size: 16px; font-weight: 500; color: #1e293b; margin: 0 0 8px 0; }
    .upload-hint { font-size: 14px; color: #94a3b8; margin: 0 0 24px 0; }
    .divider { text-align: center; position: relative; }
    .divider::before {
      content: '';
      position: absolute;
      top: 50%;
      left: 0;
      right: 0;
      height: 1px;
      background: #e2e8f0;
    }
    .divider span {
      background: #f8fafc;
      padding: 0 16px;
      color: #94a3b8;
      font-size: 12px;
      position: relative;
    }
    .paste-section label { display: block; font-size: 14px; font-weight: 500; color: #475569; margin-bottom: 8px; }
    .paste-section textarea {
      width: 100%;
      padding: 16px;
      border: 1px solid #e2e8f0;
      border-radius: 8px;
      font-family: 'Monaco', 'Menlo', monospace;
      font-size: 13px;
      line-height: 1.5;
      resize: vertical;
      box-sizing: border-box;
    }
    .paste-section textarea:focus { outline: none; border-color: #6366f1; }
    .code-preview { font-size: 13px; color: #6366f1; margin: 8px 0 0 0; }
    .file-info {
      display: flex;
      align-items: center;
      gap: 12px;
      padding: 16px;
      background: #f1f5f9;
      border-radius: 8px;
      margin: 24px 0;
    }
    .file-info span { font-size: 14px; font-weight: 500; color: #1e293b; }
    .file-size { color: #64748b !important; font-weight: 400 !important; }
    .btn-lg { width: 100%; padding: 16px; font-size: 16px; }
    .btn-lg:disabled { opacity: 0.5; cursor: not-allowed; }
    .btn-primary { background: #6366f1; color: white; border: none; border-radius: 6px; cursor: pointer; }
    .btn-primary:hover:not(:disabled) { background: #4f46e5; }
    .btn { display: inline-flex; align-items: center; justify-content: center; gap: 8px; padding: 10px 20px; font-size: 14px; font-weight: 500; }
    .existing-uploads { margin-top: 48px; }
    .existing-uploads h3 { font-size: 16px; font-weight: 600; color: #1e293b; margin: 0 0 16px 0; }
    .upload-item {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 16px;
      background: white;
      border: 1px solid #e2e8f0;
      border-radius: 8px;
      margin-bottom: 8px;
    }
    .upload-info { display: flex; flex-direction: column; gap: 4px; }
    .upload-name { font-size: 14px; font-weight: 500; color: #1e293b; }
    .upload-meta { font-size: 12px; color: #64748b; }
    .upload-date { font-size: 12px; color: #94a3b8; }
  `]
})
export class UploadComponent implements OnInit {
  projectId = '';
  pastedCode = '';
  selectedFile: File | null = null;
  isDragging = false;
  uploads: SourceUpload[] = [];

  constructor(private route: ActivatedRoute, private router: Router, private apiService: ApiService) {}

  ngOnInit() {
    this.projectId = this.route.snapshot.paramMap.get('projectId') || '';
    this.loadUploads();
  }

  loadUploads() {
    this.apiService.getProjectUploads(this.projectId).subscribe({
      next: (uploads) => this.uploads = uploads,
      error: (err) => console.error('Failed to load uploads', err)
    });
  }

  onDragOver(event: DragEvent) {
    event.preventDefault();
    this.isDragging = true;
  }

  onDragLeave(event: DragEvent) {
    this.isDragging = false;
  }

  onDrop(event: DragEvent) {
    event.preventDefault();
    this.isDragging = false;
    const files = event.dataTransfer?.files;
    if (files?.length) {
      this.selectedFile = files[0];
    }
  }

  onFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input.files?.length) {
      this.selectedFile = input.files[0];
    }
  }

  upload() {
    const sourceCode = this.pastedCode || '// File: ' + (this.selectedFile?.name || 'uploaded.java');
    this.apiService.uploadSource(this.projectId, this.selectedFile?.name || 'pasted-code.java', 
      this.selectedFile?.size || this.pastedCode.length, sourceCode).subscribe({
      next: () => {
        alert('Source code uploaded successfully!');
        this.pastedCode = '';
        this.selectedFile = null;
        this.loadUploads();
      },
      error: (err) => console.error('Upload failed', err)
    });
  }

  goBack() {
    this.router.navigate(['/']);
  }

  formatFileSize(bytes: number): string {
    if (bytes < 1024) return bytes + ' B';
    if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' KB';
    return (bytes / (1024 * 1024)).toFixed(1) + ' MB';
  }

  formatDate(date: string): string {
    return new Date(date).toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' });
  }
}