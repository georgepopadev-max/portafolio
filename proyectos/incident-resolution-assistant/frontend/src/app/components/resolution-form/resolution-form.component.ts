import { Component, Input, Output, EventEmitter, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { IncidentService } from '../../services/incident.service';

@Component({
  selector: 'app-resolution-form',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="modal-overlay" (click)="onOverlayClick($event)">
      <div class="modal-content">
        <div class="modal-header">
          <h2>Mark Incident Resolved</h2>
          <button class="close-btn" (click)="closed.emit()">×</button>
        </div>
        
        <div class="modal-body">
          <div class="form-group">
            <label for="summary">Summary</label>
            <textarea 
              id="summary"
              [(ngModel)]="summary"
              placeholder="Brief summary of the incident and resolution..."
              rows="3">
            </textarea>
          </div>
          
          <div class="form-group">
            <label for="steps">Resolution Steps</label>
            <textarea 
              id="steps"
              [(ngModel)]="steps"
              placeholder="Describe the steps taken to resolve this incident..."
              rows="5">
            </textarea>
          </div>
          
          <div class="form-group">
            <label for="outcome">Outcome</label>
            <select id="outcome" [(ngModel)]="outcome">
              <option value="RESOLVED">Resolved</option>
              <option value="WORKAROUND_APPLIED">Workaround Applied</option>
              <option value="ESCALATED">Escalated</option>
            </select>
          </div>
          
          <div class="form-group">
            <label for="jiraTicket">Jira Ticket (optional)</label>
            <input 
              type="text"
              id="jiraTicket"
              [(ngModel)]="jiraTicketId"
              placeholder="JIRA-1234">
          </div>
          
          <div class="form-group">
            <label for="resolvedBy">Resolved By</label>
            <input 
              type="text"
              id="resolvedBy"
              [(ngModel)]="resolvedBy"
              placeholder="Your name or username">
          </div>
        </div>
        
        <div class="modal-footer">
          <button class="btn-secondary" (click)="closed.emit()">Cancel</button>
          <button 
            class="btn-primary" 
            (click)="submit()"
            [disabled]="!isValid()">
            Submit Resolution
          </button>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .modal-overlay {
      position: fixed;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background: rgba(0, 0, 0, 0.7);
      display: flex;
      align-items: center;
      justify-content: center;
      z-index: 1000;
    }
    
    .modal-content {
      background: #161b22;
      border: 1px solid #30363d;
      border-radius: 12px;
      width: 90%;
      max-width: 560px;
      max-height: 90vh;
      overflow: auto;
    }
    
    .modal-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 20px 24px;
      border-bottom: 1px solid #30363d;
    }
    
    .modal-header h2 {
      margin: 0;
      font-size: 18px;
      font-weight: 600;
      color: #e6edf3;
    }
    
    .close-btn {
      background: transparent;
      border: none;
      font-size: 24px;
      color: #8b949e;
      cursor: pointer;
      padding: 0;
      line-height: 1;
    }
    
    .close-btn:hover {
      color: #e6edf3;
    }
    
    .modal-body {
      padding: 24px;
    }
    
    .form-group {
      margin-bottom: 20px;
    }
    
    .form-group label {
      display: block;
      font-size: 13px;
      font-weight: 500;
      color: #8b949e;
      margin-bottom: 8px;
    }
    
    .form-group input,
    .form-group textarea,
    .form-group select {
      width: 100%;
      background: #0d1117;
      border: 1px solid #30363d;
      border-radius: 6px;
      padding: 10px 12px;
      color: #e6edf3;
      font-size: 14px;
      font-family: inherit;
      box-sizing: border-box;
    }
    
    .form-group input:focus,
    .form-group textarea:focus,
    .form-group select:focus {
      outline: none;
      border-color: #388bfd;
    }
    
    .form-group textarea {
      resize: vertical;
      min-height: 80px;
    }
    
    .form-group select {
      cursor: pointer;
    }
    
    .modal-footer {
      display: flex;
      justify-content: flex-end;
      gap: 12px;
      padding: 16px 24px;
      border-top: 1px solid #30363d;
    }
    
    .btn-secondary {
      background: #21262d;
      border: 1px solid #30363d;
      border-radius: 6px;
      padding: 10px 16px;
      color: #e6edf3;
      font-size: 14px;
      font-weight: 500;
      cursor: pointer;
      transition: background 0.15s;
    }
    
    .btn-secondary:hover {
      background: #30363d;
    }
    
    .btn-primary {
      background: #238636;
      border: none;
      border-radius: 6px;
      padding: 10px 16px;
      color: white;
      font-size: 14px;
      font-weight: 500;
      cursor: pointer;
      transition: background 0.15s;
    }
    
    .btn-primary:hover:not(:disabled) {
      background: #2ea043;
    }
    
    .btn-primary:disabled {
      background: #21262d;
      color: #8b949e;
      cursor: not-allowed;
    }
  `]
})
export class ResolutionFormComponent {
  @Input() threadId: string | null = null;
  @Output() closed = new EventEmitter<void>();
  @Output() submitted = new EventEmitter<void>();

  summary = '';
  steps = '';
  outcome = 'RESOLVED';
  jiraTicketId = '';
  resolvedBy = '';

  private incidentService = inject(IncidentService);

  onOverlayClick(event: MouseEvent) {
    if ((event.target as HTMLElement).classList.contains('modal-overlay')) {
      this.closed.emit();
    }
  }

  isValid(): boolean {
    return this.summary.trim().length > 0 && 
           this.steps.trim().length > 0 && 
           this.resolvedBy.trim().length > 0;
  }

  submit() {
    if (!this.isValid() || !this.threadId) return;

    this.incidentService.createResolution({
      threadId: this.threadId,
      summary: this.summary,
      steps: this.steps,
      outcome: this.outcome,
      jiraTicketId: this.jiraTicketId,
      resolvedBy: this.resolvedBy
    }).subscribe({
      next: () => {
        this.submitted.emit();
      },
      error: (err) => {
        console.error('Failed to create resolution:', err);
      }
    });
  }
}
