import { isPlatformBrowser, isPlatformServer } from "@angular/common";
import { Inject, Injectable, PLATFORM_ID } from "@angular/core";

@Injectable({ providedIn: 'root' })
export class PlatformService {
  constructor(@Inject(PLATFORM_ID) private platformId: Object) { }

  isBrowser(): boolean {
    return isPlatformBrowser(this.platformId);
  }

  isServer(): boolean {
    return isPlatformServer(this.platformId);
  }

  getNativeWindow(): Window | null {
    return this.isBrowser() ? window : null;
  }

  getNativeDocument(): Document | null {
    return this.isBrowser() ? document : null;
  }

  getNavigator(): Navigator | null {
    return this.isBrowser() ? navigator : null;
  }
}
