import { Component } from '@angular/core';
import { ChatComponent } from './components/chat/chat.component';
import { ThreadSidebarComponent } from './components/thread-sidebar/thread-sidebar.component';
import { CitationCardComponent } from './components/citation-card/citation-card.component';
import { ResolutionFormComponent } from './components/resolution-form/resolution-form.component';
import { QuickActionsComponent } from './components/quick-actions/quick-actions.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    ChatComponent,
    ThreadSidebarComponent,
    CitationCardComponent,
    ResolutionFormComponent,
    QuickActionsComponent
  ],
  template: `
    <div class="app-container">
      <app-thread-sidebar
        [threads]="threads"
        [selectedThreadId]="selectedThreadId"
        (threadSelected)="onThreadSelected($event)">
      </app-thread-sidebar>
      
      <main class="main-content">
        <app-chat
          [messages]="messages"
          [isLoading]="isLoading"
          (messageSent)="onMessageSent($event)">
        </app-chat>
        
        <app-quick-actions
          [threadId]="selectedThreadId"
          [hasActiveThread]="!!selectedThreadId && hasActiveThread"
          (markResolved)="showResolutionForm = true"
          (createJira)="onCreateJira()">
        </app-quick-actions>
        
        <div class="citations-area" *ngIf="currentCitations.length > 0">
          <h3>Sources</h3>
          <div class="citations-grid">
            <app-citation-card
              *ngFor="let citation of currentCitations"
              [citation]="citation">
            </app-citation-card>
          </div>
        </div>
      </main>
      
      <app-resolution-form
        *ngIf="showResolutionForm"
        [threadId]="selectedThreadId"
        (closed)="showResolutionForm = false"
        (submitted)="onResolutionSubmitted()">
      </app-resolution-form>
    </div>
  `,
  styles: [`
    .app-container {
      display: flex;
      height: 100vh;
      background: #0d1117;
      color: #e6edf3;
      font-family: 'Inter', -apple-system, BlinkMacSystemFont, sans-serif;
    }
    
    .main-content {
      flex: 1;
      display: flex;
      flex-direction: column;
      padding: 20px;
      overflow: hidden;
    }
    
    .citations-area {
      margin-top: 20px;
      padding: 16px;
      background: #161b22;
      border-radius: 8px;
      border: 1px solid #30363d;
    }
    
    .citations-area h3 {
      margin: 0 0 16px 0;
      font-size: 14px;
      font-weight: 600;
      color: #8b949e;
      text-transform: uppercase;
      letter-spacing: 0.5px;
    }
    
    .citations-grid {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
      gap: 12px;
    }
  `]
})
export class AppComponent {
  threads: any[] = [];
  messages: any[] = [];
  selectedThreadId: string | null = null;
  currentCitations: any[] = [];
  isLoading = false;
  showResolutionForm = false;
  hasActiveThread = true;

  onThreadSelected(threadId: string) {
    this.selectedThreadId = threadId;
    // Messages will be loaded by the chat component
  }

  onMessageSent(result: { message: string; citations: any[] }) {
    this.currentCitations = result.citations;
  }

  onCreateJira() {
    console.log('Create Jira clicked');
  }

  onResolutionSubmitted() {
    this.showResolutionForm = false;
    // Refresh threads
  }
}
