import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { ApiService } from '../../services/api.service';
import { GeneratedDoc } from '../../models/project.model';

@Component({
  selector: 'app-history',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <div class="history-container">
      <button class="back-btn" (click)="goBack()">
        <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
          <path d="M10 12L6 8l4-4" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
        Back to Dashboard
      </button>

      <h1>Generation History</h1>
      <p class="subtitle">View and compare previous documentation versions</p>

      <div class="timeline">
        @for (doc of documents; track doc.id; let i = $index) {
          <div class="timeline-item" [class.latest]="i === 0">
            <div class="timeline-marker">
              <div class="marker-dot"></div>
              @if (i < documents.length - 1) {
                <div class="marker-line"></div>
              }
            </div>
            <div class="timeline-content">
              <div class="version-header">
                <span class="version-badge">v{{ doc.version }}</span>
                @if (i === 0) {
                  <span class="latest-badge">Latest</span>
                }
                <span class="timestamp">{{ formatDate(doc.generatedAt) }}</span>
              </div>
              <div class="version-details">
                <span class="model">Model: {{ doc.modelUsed }}</span>
                <span class="status" [class.completed]="doc.status === 'COMPLETED'">{{ doc.status }}</span>
              </div>
              <div class="version-actions">
                <a [routerLink]="['/editor', doc.id]" class="btn btn-sm">View</a>
                <button class="btn btn-sm" (click)="rollback(doc)">Restore</button>
              </div>
            </div>
          </div>
        } @empty {
          <div class="empty-state">
            <svg width="48" height="48" viewBox="0 0 48 48" fill="none">
              <circle cx="24" cy="24" r="20" stroke="#cbd5e1" stroke-width="2"/>
              <path d="M24 16v8l6 4" stroke="#cbd5e1" stroke-width="2" stroke-linecap="round"/>
            </svg>
            <h3>No history yet</h3>
            <p>Generate documentation to start building your version history</p>
          </div>
        }
      </div>
    </div>
  `,
  styles: [`
    .history-container { max-width: 800px; margin: 0 auto; }
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
    .timeline { padding-left: 32px; }
    .timeline-item { display: flex; margin-bottom: 32px; }
    .timeline-marker { display: flex; flex-direction: column; align-items: center; margin-right: 20px; }
    .marker-dot {
      width: 16px;
      height: 16px;
      border-radius: 50%;
      background: #e2e8f0;
      border: 3px solid white;
      box-shadow: 0 0 0 2px #e2e8f0;
    }
    .latest .marker-dot { background: #6366f1; box-shadow: 0 0 0 2px #6366f1; }
    .marker-line { width: 2px; flex: 1; background: #e2e8f0; margin-top: 8px; }
    .timeline-content {
      flex: 1;
      background: white;
      border: 1px solid #e2e8f0;
      border-radius: 8px;
      padding: 16px 20px;
    }
    .latest .timeline-content { border-color: #6366f1; }
    .version-header { display: flex; align-items: center; gap: 12px; margin-bottom: 8px; }
    .version-badge { font-size: 14px; font-weight: 600; color: #1e293b; }
    .latest-badge {
      font-size: 11px;
      font-weight: 500;
      padding: 2px 8px;
      border-radius: 4px;
      background: #6366f1;
      color: white;
    }
    .timestamp { font-size: 13px; color: #94a3b8; }
    .version-details { display: flex; gap: 16px; margin-bottom: 12px; }
    .model, .status { font-size: 13px; color: #64748b; }
    .status.completed { color: #059669; }
    .version-actions { display: flex; gap: 8px; }
    .btn { display: inline-flex; align-items: center; gap: 6px; padding: 6px 12px; border-radius: 4px; font-size: 13px; font-weight: 500; cursor: pointer; border: 1px solid #e2e8f0; background: white; color: #475569; }
    .btn:hover { background: #f8fafc; }
    .btn-sm { padding: 4px 10px; font-size: 12px; }
    .empty-state {
      text-align: center;
      padding: 48px;
      background: white;
      border-radius: 12px;
      border: 2px dashed #e2e8f0;
    }
    .empty-state svg { margin-bottom: 16px; }
    .empty-state h3 { font-size: 18px; color: #1e293b; margin: 0 0 8px 0; }
    .empty-state p { color: #64748b; margin: 0; }
  `]
})
export class HistoryComponent implements OnInit {
  projectId = '';
  documents: GeneratedDoc[] = [];

  constructor(private route: ActivatedRoute, private router: Router, private apiService: ApiService) {}

  ngOnInit() {
    this.projectId = this.route.snapshot.paramMap.get('projectId') || '';
    this.loadHistory();
  }

  loadHistory() {
    this.apiService.getProjectDocs(this.projectId).subscribe({
      next: (docs) => this.documents = docs,
      error: (err) => console.error('Failed to load history', err)
    });
  }

  rollback(doc: GeneratedDoc) {
    if (confirm(`Restore to version ${doc.version}?`)) {
      alert('Restore functionality would copy this version as the new latest');
    }
  }

  formatDate(date: string): string {
    return new Date(date).toLocaleDateString('en-US', {
      month: 'short', day: 'numeric', year: 'numeric',
      hour: '2-digit', minute: '2-digit'
    });
  }

  goBack() {
    this.router.navigate(['/']);
  }
}