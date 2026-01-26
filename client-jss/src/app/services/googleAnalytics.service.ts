import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from './appRest.service';
import { PlatformService } from './platform.service';

@Injectable({
  providedIn: 'root'
})
export class GoogleAnalyticsService {

  entryPoint: String = "miscellaneous";

  constructor(protected _http: HttpClient, private platformService: PlatformService) {
  }

  trackLoginLogout(eventName: string, pageName: string, pageType: string) {
    return this.getWithGaClientId(new HttpParams().set("eventName", eventName).set("pageName", pageName).set("pageType", pageType), "google-analytics/login-logout");
  }

  private getWithGaClientId(params: HttpParams, api: string) {
    let headers = new HttpHeaders();
    headers = headers.set('gaClientId', this.getGaClientId());
    return this._http.get(AppRestService.serverUrl + this.entryPoint + "/" + api, { params, headers });
  }

  // Even if consent is not given by the user, a cookie (anonyme) is created and will be found
  getGaClientId(): string {
    if (this.platformService.getNativeDocument() != undefined) {
      const match = this.platformService.getNativeDocument()!.cookie.match('(?:^|;)\\s*_ga=([^;]*)');
      // The format is often GA1.x.UID. We want to retrieve the UID part (from the 3rd segment)
      // Or more simply, we take everything after "GA1.x."
      if (match && match[1]) {
        const parts = match[1].split('.');
        if (parts.length >= 3) {
          return parts.slice(2).join('.'); // returns something like "123456789.987654321"
        }
      }
    }
    return ""; //  Fallback or error handling    
  }
}