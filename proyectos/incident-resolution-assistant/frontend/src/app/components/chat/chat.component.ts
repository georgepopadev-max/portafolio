import { Component, Input, Output, EventEmitter, OnChanges, SimpleChanges, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { IncidentService } from '../../services/incident.service';
import { ChatMessage, ChatResponse, Citation } from '../../models/incident.model';

@Component({
  selector: 'app-chat',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="chat-container">
      <div class="chat-header">
        <h1>Incident Resolution Assistant</h1>
        <p class="subtitle">RAG-powered incident response with contextual documentation</p>
      </div>
      
      <div class="messages-container" #messagesContainer>
        <div *ngIf="messages.length === 0" class="welcome-state">
          <div class="welcome-icon">🔍</div>
          <h2>Describe Your Incident</h2>
          <p>Enter an error message, symptom, or question below to get started. I'll retrieve relevant documentation and suggest resolution steps.</p>
          <div class="example-queries">
            <button (click)="useExampleQuery('504 gateway timeout on billing API since 18:30')">
              504 gateway timeout on billing API
            </button>
            <button (click)="useExampleQuery('Database connection pool exhausted')">
              Database connection pool exhausted
            </button>
            <button (click)="useExampleQuery('Memory leak in grid processor pods')">
              Memory leak in grid processor
            </button>
          </div>
        </div>
        
        <div *ngFor="let msg of messages" class="message" [class.user]="msg.role === 'USER'" [class.assistant]="msg.role === 'ASSISTANT'">
          <div class="message-avatar">
            <span *ngIf="msg.role === 'USER'">👤</span>
            <span *ngIf="msg.role === 'ASSISTANT'">🤖</span>
          </div>
          <div class="message-content">
            <div class="message-text" [innerHTML]="formatMessage(msg.content)"></div>
            <div class="message-time">{{ formatTime(msg.createdAt) }}</div>
          </div>
        </div>
        
        <div *ngIf="isLoading" class="message assistant">
          <div class="message-avatar">🤖</div>
          <div class="message-content">
            <div class="typing-indicator">
              <span></span>
              <span></span>
              <span></span>
            </div>
          </div>
        </div>
      </div>
      
      <div class="input-container">
        <textarea 
          [(ngModel)]="inputMessage"
          (keydown.enter)="onEnterKey($event)"
          placeholder="Describe your incident or paste an error message..."
          rows="2"
          [disabled]="isLoading">
        </textarea>
        <button 
          class="send-button" 
          (click)="sendMessage()"
          [disabled]="!inputMessage.trim() || isLoading">
          <span>Send</span>
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M22 2L11 13M22 2L15 22L11 13L2 9L22 2Z"/>
          </svg>
        </button>
      </div>
    </div>
  `,
  styles: [`
    .chat-container {
      flex: 1;
      display: flex;
      flex-direction: column;
      background: #0d1117;
      border-radius: 8px;
      border: 1px solid #30363d;
      overflow: hidden;
    }
    
    .chat-header {
      padding: 20px;
      border-bottom: 1px solid #30363d;
      background: #161b22;
    }
    
    .chat-header h1 {
      margin: 0;
      font-size: 20px;
      font-weight: 700;
      color: #e6edf3;
    }
    
    .subtitle {
      margin: 4px 0 0 0;
      font-size: 13px;
      color: #8b949e;
    }
    
    .messages-container {
      flex: 1;
      overflow-y: auto;
      padding: 20px;
    }
    
    .welcome-state {
      text-align: center;
      padding: 40px 20px;
      max-width: 500px;
      margin: 0 auto;
    }
    
    .welcome-icon {
      font-size: 48px;
      margin-bottom: 16px;
    }
    
    .welcome-state h2 {
      margin: 0 0 12px 0;
      font-size: 24px;
      color: #e6edf3;
    }
    
    .welcome-state p {
      margin: 0;
      color: #8b949e;
      line-height: 1.6;
    }
    
    .example-queries {
      margin-top: 24px;
      display: flex;
      flex-direction: column;
      gap: 8px;
    }
    
    .example-queries button {
      background: #21262d;
      border: 1px solid #30363d;
      border-radius: 6px;
      padding: 12px 16px;
      color: #e6edf3;
      font-size: 13px;
      cursor: pointer;
      text-align: left;
      transition: all 0.15s;
    }
    
    .example-queries button:hover {
      background: #30363d;
      border-color: #8b949e;
    }
    
    .message {
      display: flex;
      gap: 12px;
      margin-bottom: 20px;
    }
    
    .message.user {
      flex-direction: row-reverse;
    }
    
    .message-avatar {
      width: 36px;
      height: 36px;
      border-radius: 50%;
      background: #21262d;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 18px;
      flex-shrink: 0;
    }
    
    .message-content {
      max-width: 80%;
    }
    
    .message-text {
      background: #161b22;
      border: 1px solid #30363d;
      border-radius: 12px;
      padding: 12px 16px;
      line-height: 1.6;
      font-size: 14px;
      color: #e6edf3;
      white-space: pre-wrap;
    }
    
    .message.user .message-text {
      background: #1f6feb20;
      border-color: #1f6feb40;
    }
    
    .message-time {
      font-size: 11px;
      color: #8b949e;
      margin-top: 4px;
      padding: 0 4px;
    }
    
    .message.user .message-time {
      text-align: right;
    }
    
    .typing-indicator {
      display: flex;
      gap: 4px;
      padding: 12px 16px;
    }
    
    .typing-indicator span {
      width: 8px;
      height: 8px;
      background: #8b949e;
      border-radius: 50%;
      animation: typing 1.4s infinite;
    }
    
    .typing-indicator span:nth-child(2) { animation-delay: 0.2s; }
    .typing-indicator span:nth-child(3) { animation-delay: 0.4s; }
    
    @keyframes typing {
      0%, 60%, 100% { transform: translateY(0); opacity: 0.4; }
      30% { transform: translateY(-4px); opacity: 1; }
    }
    
    .input-container {
      padding: 16px;
      border-top: 1px solid #30363d;
      background: #161b22;
      display: flex;
      gap: 12px;
      align-items: flex-end;
    }
    
    .input-container textarea {
      flex: 1;
      background: #0d1117;
      border: 1px solid #30363d;
      border-radius: 8px;
      padding: 12px;
      color: #e6edf3;
      font-size: 14px;
      font-family: inherit;
      resize: none;
      outline: none;
    }
    
    .input-container textarea:focus {
      border-color: #388bfd;
    }
    
    .send-button {
      background: #238636;
      border: none;
      border-radius: 8px;
      padding: 12px 20px;
      color: white;
      font-size: 14px;
      font-weight: 600;
      cursor: pointer;
      display: flex;
      align-items: center;
      gap: 8px;
      transition: background 0.15s;
    }
    
    .send-button:hover:not(:disabled) {
      background: #2ea043;
    }
    
    .send-button:disabled {
      background: #21262d;
      color: #8b949e;
      cursor: not-allowed;
    }
  `]
})
export class ChatComponent implements OnChanges {
  @Input() messages: ChatMessage[] = [];
  @Input() isLoading = false;
  @Output() messageSent = new EventEmitter<{ message: string; citations: Citation[] }>();

  inputMessage = '';
  currentThreadId: string | null = null;

  private incidentService = inject(IncidentService);

  ngOnChanges(changes: SimpleChanges) {
    if (changes['messages']) {
      this.scrollToBottom();
    }
  }

  useExampleQuery(query: string) {
    this.inputMessage = query;
  }

  onEnterKey(event: KeyboardEvent) {
    if (!event.shiftKey) {
      event.preventDefault();
      this.sendMessage();
    }
  }

  sendMessage() {
    if (!this.inputMessage.trim() || this.isLoading) return;

    const message = this.inputMessage;
    this.inputMessage = '';
    this.isLoading = true;

    this.incidentService.sendMessage({
      threadId: this.currentThreadId,
      message: message
    }).subscribe({
      next: (response) => {
        this.currentThreadId = response.threadId;
        
        const userMessage: ChatMessage = {
          id: this.generateId(),
          role: 'USER',
          content: message,
          citations: '',
          createdAt: new Date()
        };
        
        const assistantMessage: ChatMessage = {
          id: this.generateId(),
          role: 'ASSISTANT',
          content: response.message,
          citations: JSON.stringify(response.citations),
          createdAt: new Date()
        };
        
        this.messages = [...this.messages, userMessage, assistantMessage];
        this.isLoading = false;
        this.messageSent.emit({ message: response.message, citations: response.citations });
        this.scrollToBottom();
      },
      error: (err) => {
        console.error('Failed to send message:', err);
        this.isLoading = false;
      }
    });
  }

  formatMessage(content: string): string {
    return content
      .replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>')
      .replace(/\n- /g, '<br>• ')
      .replace(/\n/g, '<br>');
  }

  formatTime(date: Date | string): string {
    const d = new Date(date);
    return d.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
  }

  private scrollToBottom() {
    setTimeout(() => {
      const container = document.querySelector('.messages-container');
      if (container) {
        container.scrollTop = container.scrollHeight;
      }
    }, 100);
  }

  private generateId(): string {
    return Math.random().toString(36).substring(2, 15);
  }
}
