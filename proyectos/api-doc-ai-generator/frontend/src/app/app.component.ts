import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, RouterLink],
  template: `
    <div class="app-container">
      <nav class="top-nav">
        <div class="nav-brand">
          <svg width="32" height="32" viewBox="0 0 32 32" fill="none">
            <rect width="32" height="32" rx="8" fill="#6366f1"/>
            <path d="M8 16L14 10L20 16L14 22L8 16Z" fill="white"/>
            <path d="M14 16L20 10L26 16L20 22L14 16Z" fill="white" fill-opacity="0.6"/>
          </svg>
          <span>API Doc AI Generator</span>
        </div>
        <div class="nav-links">
          <a routerLink="/" class="nav-link">Dashboard</a>
        </div>
      </nav>
      <main class="main-content">
        <router-outlet></router-outlet>
      </main>
    </div>
  `,
  styles: [`
    .app-container {
      min-height: 100vh;
      background: #f8fafc;
    }
    .top-nav {
      background: white;
      border-bottom: 1px solid #e2e8f0;
      padding: 0 24px;
      height: 64px;
      display: flex;
      align-items: center;
      justify-content: space-between;
    }
    .nav-brand {
      display: flex;
      align-items: center;
      gap: 12px;
      font-weight: 600;
      font-size: 18px;
      color: #1e293b;
    }
    .nav-links {
      display: flex;
      gap: 24px;
    }
    .nav-link {
      color: #64748b;
      text-decoration: none;
      font-size: 14px;
      font-weight: 500;
      padding: 8px 12px;
      border-radius: 6px;
      transition: all 0.2s;
    }
    .nav-link:hover {
      color: #6366f1;
      background: #f1f5f9;
    }
    .main-content {
      padding: 24px;
    }
  `]
})
export class AppComponent {}