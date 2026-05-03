import { Component, Input, Output, EventEmitter, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { IncidentService } from '../../services/incident.service';

@Component({
  selector: 'app-quick-actions',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="quick-actions" *ngIf="hasActiveThread">
      <button class="action-btn resolved" (click)="markResolved.emit()">
        <span class="icon">✓</span>
        <span>Mark Resolved</span>
      </button>
      
      <button class="action-btn jira" (click)="createJira.emit()">
        <span class="icon">📋</span>
        <span>Create Jira</span>
      </button>
      
      <button class="action-btn oncall" (click)="pageOnCall()">
        <span class="icon">🔔</span>
        <span>Page On-Call</span>
      </button>
      
      <div class="streaming-toggle">
        <label>
          <input type="checkbox" [(ngModel)]="streamingEnabled" (change)="onStreamingChange()">
          <span>Streaming mode</span>
        </label>
      </div>
    </div>
    
    <div class="quick-actions-placeholder" *ngIf="!hasActiveThread">
      <p>Start a conversation to enable quick actions</p>
    </div>
  `,
  styles: [`
    .quick-actions {
      display: flex;
      gap: 12px;
      padding: 16px;
      background: #161b22;
      border-radius: 8px;
      border: 1px solid #30363d;
      margin-top: 16px;
      align-items: center;
      flex-wrap: wrap;
    }
    
    .action-btn {
      display: flex;
      align-items: center;
      gap: 8px;
      padding: 10px 16px;
      border: none;
      border-radius: 6px;
      font-size: 14px;
      font-weight: 500;
      cursor: pointer;
      transition: all 0.15s;
    }
    
    .action-btn .icon {
      font-size: 16px;
    }
    
    .action-btn.resolved {
      background: #238636;
      color: white;
    }
    
    .action-btn.resolved:hover {
      background: #2ea043;
    }
    
    .action-btn.jira {
      background: #1f6feb;
      color: white;
    }
    
    .action-btn.jira:hover {
      background: #388bfd;
    }
    
    .action-btn.oncall {
      background: #f85149;
      color: white;
    }
    
    .action-btn.oncall:hover {
      background: #ff6b6b;
    }
    
    .streaming-toggle {
      margin-left: auto;
      display: flex;
      align-items: center;
    }
    
    .streaming-toggle label {
      display: flex;
      align-items: center;
      gap: 8px;
      font-size: 13px;
      color: #8b949e;
      cursor: pointer;
    }
    
    .streaming-toggle input[type="checkbox"] {
      width: 16px;
      height: 16px;
      accent-color: #238636;
    }
    
    .quick-actions-placeholder {
      padding: 20px;
      background: #161b22;
      border-radius: 8px;
      border: 1px solid #30363d;
      margin-top: 16px;
      text-align: center;
    }
    
    .quick-actions-placeholder p {
      margin: 0;
      color: #8b949e;
      font-size: 14px;
    }
  `]
})
export class QuickActionsComponent {
  @Input() threadId: string | null = null;
  @Input() hasActiveThread = false;
  @Output() markResolved = new EventEmitter<void>();
  @Output() createJira = new EventEmitter<void>();

  streamingEnabled = true;

  private incidentService = inject(IncidentService);

  pageOnCall() {
    if (confirm('Send PagerDuty alert for this incident?')) {
      alert('PagerDuty alert sent (simulated)');
    }
  }

  onStreamingChange() {
    console.log('Streaming mode:', this.streamingEnabled);
  }
}
