import { Injectable } from "@angular/core";
import { PlatformService } from "./platform.service";

@Injectable({ providedIn: 'root' })
export class GtmService {
  constructor(private platformService: PlatformService) { }

  init() {
    if (this.platformService.isBrowser() && this.platformService.getNativeDocument() && this.platformService.getNativeWindow()) {
      const script = this.platformService.getNativeDocument()!.createElement('script');
      script.async = true;
      script.src = 'https://www.googletagmanager.com/gtm.js?id=GTM-MQRHV8GR';
      this.platformService.getNativeDocument()!.head.appendChild(script);

      (this.platformService.getNativeWindow()! as any).dataLayer = (this.platformService.getNativeWindow()! as any).dataLayer || [];
      (this.platformService.getNativeWindow()! as any).dataLayer.push({
        'gtm.start': new Date().getTime(),
        event: 'gtm.js'
      });
    }
  }
}
