import { Injectable } from "@angular/core";
import { Router } from "@angular/router";
import { environment } from "../../environments/environment";
import { Responsable } from "../main/model/Responsable";
import { LoginService } from "../main/services/login.service";
import { CookieService } from "./cookie.service";
import { BasePayload, CtaClickPayload, FormSubmitPayload, PageInfo, PageViewPayload } from "./GtmPayload";
import { PlatformService } from "./platform.service";

export enum GtmEventName {
  PageView = 'page_view',
  CtaClick = 'cta_click',
  FormSubmit = 'form_submit',
  FileUpload = 'file_upload',
  Purchase = 'purchase',
  Login = 'login',
  Logout = 'logout',
}

@Injectable({ providedIn: 'root' })
export class GtmService {

  private gtmId = 'GTM-MQRHV8GR';
  private isInitialized = false;
  currentUser: Responsable | undefined;

  constructor(private platformService: PlatformService,
    private loginService: LoginService,
    private cookieService: CookieService,
    private router: Router
  ) { }

  init() {
    if (!this.platformService.isBrowser() || this.isInitialized) return;

    const doc = this.platformService.getNativeDocument()!;
    const win = this.platformService.getNativeWindow()! as any;

    // Initialiser dataLayer
    win.dataLayer = win.dataLayer || [];
    win.dataLayer.push({ 'gtm.start': new Date().getTime(), event: 'gtm.js' });

    const script = doc.createElement('script');
    script.async = true;
    script.src = `https://www.googletagmanager.com/gtm.js?id=${this.gtmId}`;
    doc.head.appendChild(script);

    this.loginService.getCurrentUser().subscribe(response => {
      this.currentUser = response;
    })

    this.push(GtmEventName.PageView, { page: { type: "page", name: this.router.url } as PageInfo } as PageViewPayload);

    this.isInitialized = true;
  }

  private push(event: GtmEventName, payload: BasePayload) {
    const win = this.platformService.getNativeWindow()! as any;
    if (!this.platformService.isBrowser() || !win.dataLayer) return;

    if (this.currentUser)
      payload.user = { id: this.currentUser.id };
    payload.consent = this.cookieService.getConsent() == true;

    if (payload && payload.page)
      payload.page.website = "media";

    const eventData = { event, ...payload };
    win.dataLayer.push(eventData);

    if (!environment.production) {
      console.log('[GTM]', eventData);
    }
  }

  trackCtaClick(payload: CtaClickPayload) {
    this.push(GtmEventName.CtaClick, payload);
  }

  trackFormSubmit(payload: FormSubmitPayload) {
    this.push(GtmEventName.FormSubmit, payload);
  }
}
