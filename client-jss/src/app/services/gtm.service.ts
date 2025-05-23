import { Injectable } from "@angular/core";
import { PlatformService } from "./platform.service";

@Injectable({ providedIn: 'root' })
export class GtmService {
  constructor(private platformService: PlatformService) { }

  init() {
    if (this.platformService.isBrowser()) {
      const script = document.createElement('script');
      script.async = true;
      script.src = 'https://www.googletagmanager.com/gtm.js?id=GTM-MQRHV8GR';
      document.head.appendChild(script);

      (window as any).dataLayer = (window as any).dataLayer || [];
      (window as any).dataLayer.push({
        'gtm.start': new Date().getTime(),
        event: 'gtm.js'
      });
    }
  }
}
