import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../services/api.service';
import { Project, CreateProjectRequest } from '../../models/project.model';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  template: `
    <div class="dashboard">
      <header class="dashboard-header">
        <h1>Projects</h1>
        <button class="btn btn-primary" (click)="showCreateModal = true">
          <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
            <path d="M8 3v10M3 8h10" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
          </svg>
          New Project
        </button>
      </header>

      <div class="projects-grid">
        @for (project of projects; track project.id) {
          <div class="project-card">
            <div class="project-header">
              <h3>{{ project.name }}</h3>
              <span class="status-badge" [class.ready]="project.status === 'READY'">
                {{ project.status }}
              </span>
            </div>
            <p class="project-description">{{ project.description || 'No description' }}</p>
            <div class="project-stats">
              <div class="stat">
                <span class="stat-value">{{ project.endpointCount }}</span>
                <span class="stat-label">Endpoints</span>
              </div>
              <div class="stat">
                <span class="stat-value">{{ project.aiProvider }}</span>
                <span class="stat-label">AI Provider</span>
              </div>
            </div>
            @if (project.lastGenerated) {
              <div class="last-generated">
                Last generated: {{ formatDate(project.lastGenerated) }}
              </div>
            }
            <div class="project-actions">
              <a [routerLink]="['/upload', project.id]" class="btn btn-sm">Upload Code</a>
              @if (project.endpointCount > 0) {
                <a [routerLink]="['/generate', project.id]" class="btn btn-sm btn-primary">Generate Docs</a>
                <a [routerLink]="['/history', project.id]" class="btn btn-sm">History</a>
              }
              <button class="btn btn-sm btn-danger" (click)="deleteProject(project.id)">Delete</button>
            </div>
          </div>
        } @empty {
          <div class="empty-state">
            <svg width="64" height="64" viewBox="0 0 64 64" fill="none">
              <rect x="12" y="8" width="40" height="48" rx="4" stroke="#cbd5e1" stroke-width="2"/>
              <path d="M20 20h24M20 28h24M20 36h16" stroke="#cbd5e1" stroke-width="2" stroke-linecap="round"/>
            </svg>
            <h3>No projects yet</h3>
            <p>Create your first project to start generating API documentation</p>
            <button class="btn btn-primary" (click)="showCreateModal = true">Create Project</button>
          </div>
        }
      </div>

      @if (showCreateModal) {
        <div class="modal-overlay" (click)="showCreateModal = false">
          <div class="modal" (click)="$event.stopPropagation()">
            <h2>Create New Project</h2>
            <form (ngSubmit)="createProject()">
              <div class="form-group">
                <label>Project Name</label>
                <input type="text" [(ngModel)]="newProject.name" name="name" required 
                       placeholder="e.g., Billing Service API">
              </div>
              <div class="form-group">
                <label>Description</label>
                <textarea [(ngModel)]="newProject.description" name="description" 
                          placeholder="Brief description of the project" rows="3"></textarea>
              </div>
              <div class="form-group">
                <label>AI Provider</label>
                <select [(ngModel)]="newProject.aiProvider" name="aiProvider">
                  <option value="mock">Mock AI (Demo)</option>
                  <option value="openai">OpenAI GPT-4</option>
                </select>
              </div>
              <div class="modal-actions">
                <button type="button" class="btn" (click)="showCreateModal = false">Cancel</button>
                <button type="submit" class="btn btn-primary">Create Project</button>
              </div>
            </form>
          </div>
        </div>
      }
    </div>
  `,
  styles: [`
    .dashboard { max-width: 1200px; margin: 0 auto; }
    .dashboard-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 32px;
    }
    .dashboard-header h1 { font-size: 28px; font-weight: 700; color: #1e293b; margin: 0; }
    .projects-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(340px, 1fr)); gap: 24px; }
    .project-card {
      background: white;
      border-radius: 12px;
      padding: 24px;
      border: 1px solid #e2e8f0;
      transition: all 0.2s;
    }
    .project-card:hover { border-color: #6366f1; box-shadow: 0 4px 12px rgba(99, 102, 241, 0.1); }
    .project-header { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 12px; }
    .project-header h3 { font-size: 18px; font-weight: 600; color: #1e293b; margin: 0; }
    .status-badge {
      font-size: 12px;
      font-weight: 500;
      padding: 4px 8px;
      border-radius: 4px;
      background: #fef3c7;
      color: #d97706;
    }
    .status-badge.ready { background: #d1fae5; color: #059669; }
    .project-description { color: #64748b; font-size: 14px; margin: 0 0 16px 0; line-height: 1.5; }
    .project-stats { display: flex; gap: 24px; margin-bottom: 16px; }
    .stat { display: flex; flex-direction: column; }
    .stat-value { font-size: 20px; font-weight: 600; color: #1e293b; }
    .stat-label { font-size: 12px; color: #64748b; text-transform: uppercase; letter-spacing: 0.5px; }
    .last-generated { font-size: 12px; color: #94a3b8; margin-bottom: 16px; }
    .project-actions { display: flex; gap: 8px; flex-wrap: wrap; }
    .btn {
      display: inline-flex;
      align-items: center;
      gap: 6px;
      padding: 8px 16px;
      border-radius: 6px;
      font-size: 14px;
      font-weight: 500;
      cursor: pointer;
      border: none;
      transition: all 0.2s;
      text-decoration: none;
    }
    .btn-sm { padding: 6px 12px; font-size: 13px; }
    .btn-primary { background: #6366f1; color: white; }
    .btn-primary:hover { background: #4f46e5; }
    .btn-danger { background: #fee2e2; color: #dc2626; }
    .btn-danger:hover { background: #fecaca; }
    .btn:not(.btn-primary):not(.btn-danger) { background: #f1f5f9; color: #475569; }
    .btn:not(.btn-primary):not(.btn-danger):hover { background: #e2e8f0; }
    .empty-state {
      grid-column: 1 / -1;
      text-align: center;
      padding: 64px 24px;
      background: white;
      border-radius: 12px;
      border: 2px dashed #e2e8f0;
    }
    .empty-state svg { margin-bottom: 16px; }
    .empty-state h3 { font-size: 18px; color: #1e293b; margin: 0 0 8px 0; }
    .empty-state p { color: #64748b; margin: 0 0 24px 0; }
    .modal-overlay {
      position: fixed;
      inset: 0;
      background: rgba(0,0,0,0.5);
      display: flex;
      align-items: center;
      justify-content: center;
      z-index: 1000;
    }
    .modal {
      background: white;
      border-radius: 12px;
      padding: 32px;
      width: 100%;
      max-width: 480px;
      box-shadow: 0 20px 25px -5px rgba(0,0,0,0.1);
    }
    .modal h2 { font-size: 20px; font-weight: 600; color: #1e293b; margin: 0 0 24px 0; }
    .form-group { margin-bottom: 16px; }
    .form-group label { display: block; font-size: 14px; font-weight: 500; color: #475569; margin-bottom: 6px; }
    .form-group input, .form-group textarea, .form-group select {
      width: 100%;
      padding: 10px 12px;
      border: 1px solid #e2e8f0;
      border-radius: 6px;
      font-size: 14px;
      box-sizing: border-box;
    }
    .form-group input:focus, .form-group textarea:focus, .form-group select:focus {
      outline: none;
      border-color: #6366f1;
      box-shadow: 0 0 0 3px rgba(99, 102, 241, 0.1);
    }
    .modal-actions { display: flex; justify-content: flex-end; gap: 12px; margin-top: 24px; }
  `]
})
export class DashboardComponent implements OnInit {
  projects: Project[] = [];
  showCreateModal = false;
  newProject: CreateProjectRequest = { name: '', description: '', aiProvider: 'mock' };

  constructor(private apiService: ApiService) {}

  ngOnInit() {
    this.loadProjects();
  }

  loadProjects() {
    this.apiService.getProjects().subscribe({
      next: (projects) => this.projects = projects,
      error: (err) => console.error('Failed to load projects', err)
    });
  }

  createProject() {
    this.apiService.createProject(this.newProject).subscribe({
      next: () => {
        this.showCreateModal = false;
        this.newProject = { name: '', description: '', aiProvider: 'mock' };
        this.loadProjects();
      },
      error: (err) => console.error('Failed to create project', err)
    });
  }

  deleteProject(id: string) {
    if (confirm('Delete this project?')) {
      this.apiService.deleteProject(id).subscribe({
        next: () => this.loadProjects(),
        error: (err) => console.error('Failed to delete project', err)
      });
    }
  }

  formatDate(date: string): string {
    return new Date(date).toLocaleDateString('en-US', { 
      month: 'short', day: 'numeric', year: 'numeric', hour: '2-digit', minute: '2-digit'
    });
  }
}