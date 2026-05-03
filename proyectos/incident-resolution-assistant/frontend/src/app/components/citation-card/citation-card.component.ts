import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Citation } from '../../models/incident.model';

@Component({
  selector: 'app-citation-card',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="citation-card" [class]="'source-' + citation.sourceType">
      <div class="citation-header">
        <span class="source-type">{{ citation.sourceType }}</span>
        <span class="relevance-score">{{ (citation.relevanceScore * 100).toFixed(0) }}% match</span>
      </div>
      <h4 class="citation-title">{{ citation.title }}</h4>
      <p class="citation-snippet">{{ citation.snippet }}</p>
      <div class="citation-footer">
        <button class="expand-btn">View Full Document</button>
      </div>
    </div>
  `,
  styles: [`
    .citation-card {
      background: #21262d;
      border: 1px solid #30363d;
      border-radius: 8px;
      padding: 14px;
      transition: border-color 0.15s;
    }
    
    .citation-card:hover {
      border-color: #8b949e;
    }
    
    .citation-card.source-incident {
      border-left: 3px solid #f85149;
    }
    
    .citation-card.source-runbook {
      border-left: 3px solid #a371f7;
    }
    
    .citation-card.source-architecture {
      border-left: 3px solid #58a6ff;
    }
    
    .citation-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 8px;
    }
    
    .source-type {
      font-size: 10px;
      font-weight: 600;
      text-transform: uppercase;
      letter-spacing: 0.5px;
      color: #8b949e;
      background: #30363d;
      padding: 3px 8px;
      border-radius: 4px;
    }
    
    .relevance-score {
      font-size: 12px;
      font-weight: 600;
      color: #3fb950;
    }
    
    .citation-title {
      margin: 0 0 8px 0;
      font-size: 14px;
      font-weight: 600;
      color: #e6edf3;
      line-height: 1.4;
    }
    
    .citation-snippet {
      margin: 0 0 12px 0;
      font-size: 13px;
      color: #8b949e;
      line-height: 1.5;
      display: -webkit-box;
      -webkit-line-clamp: 3;
      -webkit-box-orient: vertical;
      overflow: hidden;
    }
    
    .citation-footer {
      display: flex;
      justify-content: flex-end;
    }
    
    .expand-btn {
      background: transparent;
      border: 1px solid #30363d;
      border-radius: 6px;
      padding: 6px 12px;
      color: #8b949e;
      font-size: 12px;
      cursor: pointer;
      transition: all 0.15s;
    }
    
    .expand-btn:hover {
      background: #30363d;
      color: #e6edf3;
    }
  `]
})
export class CitationCardComponent {
  @Input() citation!: Citation;
}
