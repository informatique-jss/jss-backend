import { Injectable } from '@angular/core';
import { environment } from '../../../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class CookieService {

  private readonly COOKIE_NAME = 'cookie_consent';
  private readonly MAX_AGE = 1000 * 60 * 60 * 24 * 365; // 1 year
  private readonly JSS_DOMAIN = environment.cookieDomain;

  constructor() { }

  setConsent(consent: boolean): void {
    const value = consent ? true : false;
    let expiringDate: Date = new Date();
    expiringDate.setTime(expiringDate.getTime() + this.MAX_AGE)
    document.cookie = this.COOKIE_NAME + "=" + value + "; " + "expires=" + expiringDate.toUTCString() + ";" + "path=" + "/" + ";" + "domain=" + this.JSS_DOMAIN + ";" + "SameSite=Lax";
  }

  getConsent(): boolean | null {
    const match = document.cookie.match(
      new RegExp(`(^| )${this.COOKIE_NAME}=([^;]+)`)
    );

    if (!match) {
      return null;
    }

    return match[2] === 'true';
  }

  resetCookies() {
    document.cookie = this.COOKIE_NAME + "=";
  }
}
