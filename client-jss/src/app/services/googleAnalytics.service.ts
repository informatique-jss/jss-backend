import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from './appRest.service';
import { PlatformService } from './platform.service';

@Injectable({
  providedIn: 'root'
})
export class GoogleAnalyticsService extends AppRestService<string> {

  measurementId = "0BY856TTFM";

  constructor(http: HttpClient, private platformService: PlatformService) {
    super(http, "miscellaneous");
  }

  trackLoginLogout(eventName: string, pageName: string, pageType: string) {
    return this.get(new HttpParams().set("eventName", eventName).set("pageName", pageName).set("pageType", pageType)
      .set("gaClientId", this.getAnalyticsIds().clientId ? this.getAnalyticsIds().clientId! : "")
      .set("gaSessionId", this.getAnalyticsIds().sessionId ? this.getAnalyticsIds().sessionId! : ""), "google-analytics/login-logout");
  }

  private getCookie(name: string): string | null {
    const nameLenPlus = (name.length + 1);
    return document.cookie
      .split(';')
      .map(c => c.trim())
      .filter(cookie => cookie.substring(0, nameLenPlus) === `${name}=`)
      .map(cookie => decodeURIComponent(cookie.substring(nameLenPlus)))[0] || null;
  }

  getAnalyticsIds() {
    const cleanId = this.measurementId.replace('G-', '');
    let sessionCookie = this.getCookie(`_ga_${cleanId}`);
    if (sessionCookie == null)
      sessionCookie = this.getCookie("_ga_XXXXXXXXX");
    const clientCookie = this.getCookie('_ga');

    if (sessionCookie) {
      const parts = sessionCookie.split('.');
      const sessionId = this.extractSessionId(parts[2]);

      const clientId = clientCookie ? clientCookie.split('.').slice(-2).join('.') : null;

      return { clientId, sessionId };
    }
    return {};
  }

  extractSessionId(cookieValue: string): string | null {
    if (cookieValue.includes('$')) {
      const parts = cookieValue.split('$');
      const sPart = parts.find(p => p.startsWith('s'));
      return sPart ? sPart.substring(1) : null; // On enlève le 's'
    }

    if (cookieValue.includes('.')) {
      const parts = cookieValue.split('.');
      return parts[2] || null;
    }

    return null;
  }
}
