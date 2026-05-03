import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { GenerationService } from '../../services/generation.service';
import { ApiService } from '../../services/api.service';
import { GenerationStatus, GenerationParams, GeneratedDoc } from '../../models/project.model';

@Component({
  selector: 'app-generation',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  template: `
    <div class="generation-container">
      <button class="back-btn" (click)="goBack()">
        <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
          <path d="M10 12L6 8l4-4" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
        Back to Project
      </button>

      <h1>Generate Documentation</h1>
      <p class="subtitle">Configure AI parameters and generate OpenAPI documentation</p>

      <div class="config-section">
        <h3>AI Configuration</h3>
        <div class="form-row">
          <div class="form-group">
            <label>Model</label>
            <select [(ngModel)]="params.model">
              <option value="mock-gpt-4">Mock GPT-4 (Demo)</option>
              <option value="gpt-4">OpenAI GPT-4</option>
              <option value="gpt-3.5-turbo">GPT-3.5 Turbo</option>
            </select>
          </div>
          <div class="form-group">
            <label>Detail Level</label>
            <select [(ngModel)]="params.detailLevel">
              <option value="brief">Brief</option>
              <option value="standard">Standard</option>
              <option value="detailed">Detailed</option>
            </select>
          </div>
        </div>
        <div class="form-row">
          <div class="form-group">
            <label>Temperature: {{ params.temperature }}</label>
            <input type="range" [(ngModel)]="params.temperature" min="0" max="1" step="0.1">
          </div>
          <div class="form-group">
            <label>Max Tokens: {{ params.maxTokens }}</label>
            <input type="range" [(ngModel)]="params.maxTokens" min="500" max="4000" step="100">
          </div>
        </div>
      </div>

      @if (generationStatus.status !== 'COMPLETED') {
        <div class="generation-panel">
          @if (generationStatus.status === 'IDLE') {
            <button class="btn btn-primary btn-lg" (click)="startGeneration()">
              <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
                <path d="M10 2l2.5 6.5H19l-5.5 4 2 6.5L10 15l-5.5 4 2-6.5L1 8.5h6.5z" fill="currentColor"/>
              </svg>
              Start Generation
            </button>
          } @else {
            <div class="progress-section">
              <div class="progress-header">
                <span class="step-indicator">{{ generationStatus.stepNumber }} / {{ generationStatus.totalSteps }}</span>
                <span class="step-label">{{ generationStatus.currentStep }}</span>
              </div>
              <div class="progress-bar">
                <div class="progress-fill" [style.width.%]="(generationStatus.stepNumber / generationStatus.totalSteps) * 100"></div>
              </div>
              @if (generationStatus.aiRequestsProcessed > 0) {
                <p class="ai-requests">AI processing: {{ generationStatus.aiRequestsProcessed }} requests</p>
              }
            </div>
          }
        </div>
      } @else {
        <div class="success-panel">
          <svg width="48" height="48" viewBox="0 0 48 48" fill="none">
            <circle cx="24" cy="24" r="20" stroke="#059669" stroke-width="3"/>
            <path d="M16 24l6 6 12-12" stroke="#059669" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
          <h3>Documentation Generated!</h3>
          <p>Version {{ generatedDoc?.version }} - {{ generationStatus.message }}</p>
          <div class="success-actions">
            <a [routerLink]="['/editor', generatedDoc?.id]" class="btn btn-primary">Edit Documentation</a>
            <button class="btn" (click)="startGeneration()">Regenerate</button>
          </div>
        </div>
      }
    </div>
  `,
  styles: [`
    .generation-container { max-width: 800px; margin: 0 auto; }
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
    .config-section {
      background: white;
      border-radius: 12px;
      padding: 24px;
      border: 1px solid #e2e8f0;
      margin-bottom: 24px;
    }
    .config-section h3 { font-size: 16px; font-weight: 600; color: #1e293b; margin: 0 0 20px 0; }
    .form-row { display: flex; gap: 24px; margin-bottom: 16px; }
    .form-group { flex: 1; }
    .form-group label { display: block; font-size: 14px; font-weight: 500; color: #475569; margin-bottom: 8px; }
    .form-group select, .form-group input[type="range"] { width: 100%; }
    .form-group select {
      padding: 10px 12px;
      border: 1px solid #e2e8f0;
      border-radius: 6px;
      font-size: 14px;
      background: white;
    }
    .form-group select:focus { outline: none; border-color: #6366f1; }
    .generation-panel {
      background: white;
      border-radius: 12px;
      padding: 48px;
      border: 1px solid #e2e8f0;
      text-align: center;
    }
    .btn-lg { padding: 16px 32px; font-size: 16px; }
    .btn-primary { background: #6366f1; color: white; border: none; border-radius: 6px; cursor: pointer; display: inline-flex; align-items: center; gap: 8px; text-decoration: none; }
    .btn-primary:hover { background: #4f46e5; }
    .btn { padding: 10px 20px; border-radius: 6px; font-size: 14px; font-weight: 500; cursor: pointer; border: 1px solid #e2e8f0; background: white; color: #475569; }
    .btn:hover { background: #f8fafc; }
    .progress-section { text-align: left; }
    .progress-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px; }
    .step-indicator { font-size: 14px; font-weight: 600; color: #6366f1; }
    .step-label { font-size: 14px; color: #475569; }
    .progress-bar { height: 8px; background: #e2e8f0; border-radius: 4px; overflow: hidden; }
    .progress-fill { height: 100%; background: linear-gradient(90deg, #6366f1, #8b5cf6); transition: width 0.3s ease; }
    .ai-requests { font-size: 13px; color: #64748b; margin: 12px 0 0 0; }
    .success-panel {
      background: white;
      border-radius: 12px;
      padding: 48px;
      border: 1px solid #d1fae5;
      text-align: center;
    }
    .success-panel svg { margin-bottom: 16px; }
    .success-panel h3 { font-size: 20px; font-weight: 600; color: #059669; margin: 0 0 8px 0; }
    .success-panel p { color: #64748b; margin: 0 0 24px 0; }
    .success-actions { display: flex; gap: 12px; justify-content: center; }
  `]
})
export class GenerationComponent implements OnInit {
  projectId = '';
  params: GenerationParams = { temperature: 0.7, maxTokens: 2000, detailLevel: 'standard', model: 'mock-gpt-4' };
  generationStatus: GenerationStatus = { status: 'IDLE', currentStep: '', stepNumber: 0, totalSteps: 4, aiRequestsProcessed: 0, message: '' };
  generatedDoc: GeneratedDoc | null = null;

  private pollInterval: any;

  constructor(private route: ActivatedRoute, private router: Router, 
              private generationService: GenerationService, private apiService: ApiService) {}

  ngOnInit() {
    this.projectId = this.route.snapshot.paramMap.get('projectId') || '';
    this.checkExistingDoc();
  }

  checkExistingDoc() {
    this.apiService.getLatestDoc(this.projectId).subscribe({
      next: (doc) => {
        this.generatedDoc = doc;
        this.generationStatus = { status: 'COMPLETED', currentStep: 'Done', stepNumber: 4, totalSteps: 4, aiRequestsProcessed: 0, message: `Generated v${doc.version}` };
      },
      error: () => {}
    });
  }

  startGeneration() {
    this.generationService.startGeneration(this.projectId, this.params).subscribe({
      next: (doc) => {
        this.generatedDoc = doc;
        this.generationStatus = { status: 'COMPLETED', currentStep: 'Done', stepNumber: 4, totalSteps: 4, aiRequestsProcessed: 0, message: `Generated v${doc.version}` };
      },
      error: (err) => console.error('Generation failed', err)
    });
  }

  goBack() {
    this.router.navigate(['/']);
  }

  ngOnDestroy() {
    if (this.pollInterval) clearInterval(this.pollInterval);
  }
}