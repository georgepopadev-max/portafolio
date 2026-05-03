import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ExportService } from '../../services/export.service';
import { ApiService } from '../../services/api.service';
import { GeneratedDoc } from '../../models/project.model';

@Component({
  selector: 'app-editor',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  template: `
    <div class="editor-container">
      <header class="editor-header">
        <div class="header-left">
          <button class="back-btn" (click)="goBack()">
            <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
              <path d="M10 12L6 8l4-4" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
            Back
          </button>
          <h1>Documentation Editor</h1>
        </div>
        <div class="header-actions">
          <select [(ngModel)]="format" class="format-select">
            <option value="yaml">YAML</option>
            <option value="json">JSON</option>
          </select>
          <button class="btn" (click)="validateSpec()">Validate</button>
          <button class="btn" (click)="downloadSpec()">
            <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
              <path d="M8 12V4M8 4l-4 4M8 4l4 4" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M2 14h12" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
            </svg>
            Download {{ format.toUpperCase() }}
          </button>
        </div>
      </header>

      @if (validationResult) {
        <div class="validation-banner" [class.valid]="isValid" [class.invalid]="!isValid">
          {{ validationResult }}
        </div>
      }

      <div class="editor-layout">
        <div class="code-editor">
          <textarea [(ngModel)]="specContent" (input)="onContentChange()"
                    spellcheck="false" class="spec-textarea"></textarea>
        </div>
        <div class="preview-panel">
          <h3>Preview</h3>
          <div class="preview-content" [innerHTML]="previewHtml"></div>
        </div>
      </div>

      <div class="editor-footer">
        <button class="btn btn-secondary" (click)="resetSpec()">Reset to Original</button>
        <button class="btn btn-primary" (click)="saveSpec()">Save Changes</button>
      </div>
    </div>
  `,
  styles: [`
    .editor-container { display: flex; flex-direction: column; height: calc(100vh - 112px); }
    .editor-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding-bottom: 16px;
      border-bottom: 1px solid #e2e8f0;
      margin-bottom: 16px;
    }
    .header-left { display: flex; align-items: center; gap: 16px; }
    .back-btn {
      display: inline-flex;
      align-items: center;
      gap: 6px;
      color: #64748b;
      background: none;
      border: none;
      cursor: pointer;
      font-size: 14px;
    }
    .back-btn:hover { color: #6366f1; }
    h1 { font-size: 20px; font-weight: 600; color: #1e293b; margin: 0; }
    .header-actions { display: flex; gap: 12px; align-items: center; }
    .format-select {
      padding: 8px 12px;
      border: 1px solid #e2e8f0;
      border-radius: 6px;
      font-size: 14px;
      background: white;
    }
    .validation-banner {
      padding: 12px 16px;
      border-radius: 8px;
      font-size: 14px;
      font-weight: 500;
      margin-bottom: 16px;
    }
    .validation-banner.valid { background: #d1fae5; color: #059669; }
    .validation-banner.invalid { background: #fee2e2; color: #dc2626; }
    .editor-layout { display: flex; flex: 1; gap: 16px; min-height: 0; }
    .code-editor { flex: 1; min-width: 0; }
    .spec-textarea {
      width: 100%;
      height: 100%;
      padding: 16px;
      border: 1px solid #e2e8f0;
      border-radius: 8px;
      font-family: 'Monaco', 'Menlo', monospace;
      font-size: 13px;
      line-height: 1.6;
      resize: none;
      background: #1e1e1e;
      color: #d4d4d4;
    }
    .spec-textarea:focus { outline: none; border-color: #6366f1; }
    .preview-panel {
      flex: 1;
      background: white;
      border: 1px solid #e2e8f0;
      border-radius: 8px;
      padding: 16px;
      overflow-y: auto;
    }
    .preview-panel h3 { font-size: 14px; font-weight: 600; color: #1e293b; margin: 0 0 16px 0; }
    .preview-content { font-size: 13px; line-height: 1.6; color: #475569; }
    .preview-content :global(.path-item) { margin-bottom: 16px; }
    .preview-content :global(.method) { 
      display: inline-block;
      padding: 2px 8px;
      border-radius: 4px;
      font-size: 11px;
      font-weight: 600;
      margin-right: 8px;
    }
    .preview-content :global(.get) { background: #d1fae5; color: #059669; }
    .preview-content :global(.post) { background: #dbeafe; color: #2563eb; }
    .preview-content :global(.put) { background: #fef3c7; color: #d97706; }
    .preview-content :global(.delete) { background: #fee2e2; color: #dc2626; }
    .editor-footer {
      display: flex;
      justify-content: flex-end;
      gap: 12px;
      padding-top: 16px;
      border-top: 1px solid #e2e8f0;
      margin-top: 16px;
    }
    .btn { display: inline-flex; align-items: center; gap: 6px; padding: 10px 16px; border-radius: 6px; font-size: 14px; font-weight: 500; cursor: pointer; border: 1px solid #e2e8f0; background: white; color: #475569; }
    .btn:hover { background: #f8fafc; }
    .btn-primary { background: #6366f1; color: white; border: none; }
    .btn-primary:hover { background: #4f46e5; }
    .btn-secondary { background: #f1f5f9; }
  `]
})
export class EditorComponent implements OnInit {
  docId = '';
  specContent = '';
  originalContent = '';
  format: 'yaml' | 'json' = 'yaml';
  validationResult = '';
  isValid = false;
  previewHtml = '';

  constructor(private route: ActivatedRoute, private router: Router, 
              private exportService: ExportService, private apiService: ApiService) {}

  ngOnInit() {
    this.docId = this.route.snapshot.paramMap.get('docId') || '';
    this.loadDoc();
  }

  loadDoc() {
    this.apiService.getLatestDoc('').subscribe({
      next: () => {},
      error: () => {}
    });

    this.exportService.exportYaml(this.docId).subscribe({
      next: (yaml) => {
        this.specContent = yaml;
        this.originalContent = yaml;
        this.updatePreview();
      },
      error: (err) => console.error('Failed to load spec', err)
    });
  }

  onContentChange() {
    this.updatePreview();
  }

  updatePreview() {
    const lines = this.specContent.split('\n');
    let html = '';
    let inPath = false;
    let currentPath = '';

    for (const line of lines) {
      if (line.match(/^\s{0,4}\/\w+:?$/)) {
        currentPath = line.replace(/^\s+/, '').replace(/:$/, '');
        inPath = true;
        html += `<div class="path-item"><strong>${currentPath}</strong>`;
      } else if (line.match(/^\s+get:\s*/i)) {
        html += `<span class="method get">GET</span>`;
      } else if (line.match(/^\s+post:\s*/i)) {
        html += `<span class="method post">POST</span>`;
      } else if (line.match(/^\s+put:\s*/i)) {
        html += `<span class="method put">PUT</span>`;
      } else if (line.match(/^\s+delete:\s*/i)) {
        html += `<span class="method delete">DELETE</span>`;
      } else if (line.match(/^\s+summary:/)) {
        const match = line.match(/summary:\s*"?(.+?)"?\s*$/);
        if (match) {
          html += `<div style="margin: 4px 0 8px 24px">${match[1]}</div>`;
        }
      } else if (line.match(/^\s+description:/)) {
        const match = line.match(/description:\s*"?(.+?)"?\s*$/);
        if (match) {
          html += `<div style="margin-left: 24px; color: #64748b">${match[1]}</div>`;
        }
      }
    }

    this.previewHtml = html || '<p style="color: #94a3b8">No paths defined</p>';
  }

  validateSpec() {
    this.exportService.validateYaml(this.specContent).subscribe({
      next: (result) => {
        this.validationResult = result;
        this.isValid = true;
      },
      error: (err) => {
        this.validationResult = err.error || 'Invalid YAML';
        this.isValid = false;
      }
    });
  }

  downloadSpec() {
    if (this.format === 'yaml') {
      this.exportService.downloadYaml(this.docId, 'openapi.yaml');
    } else {
      this.exportService.downloadJson(this.docId, 'openapi.json');
    }
  }

  resetSpec() {
    this.specContent = this.originalContent;
    this.updatePreview();
  }

  saveSpec() {
    this.exportService.updateSpec(this.docId, this.specContent).subscribe({
      next: () => {
        alert('Specification saved successfully!');
        this.originalContent = this.specContent;
      },
      error: (err) => console.error('Failed to save', err)
    });
  }

  goBack() {
    this.router.navigate(['/']);
  }
}