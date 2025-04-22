import { Injectable } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class ThemeService {
  private themeLinkId = 'bootstrap-theme'; // ID pour retrouver le <link>
  private currentTheme: string | null = null;

  setCssTheme(themeFile: string): void {
    if (this.currentTheme === themeFile) return;

    let linkEl = document.getElementById(this.themeLinkId) as HTMLLinkElement;

    if (!linkEl) {
      linkEl = document.createElement('link');
      linkEl.rel = 'stylesheet';
      linkEl.id = this.themeLinkId;
      document.head.appendChild(linkEl);
    }

    linkEl.href = `/assets/css/${themeFile}`;
    this.currentTheme = themeFile;
  }

  updateThemeFromUrl(url: string): void {
    if ((url.includes('/account')) && !url.includes("/signin")) {
      this.setCssTheme('theme-account.css');

    } else {
      this.setCssTheme('theme.css');
    }
  }
}
