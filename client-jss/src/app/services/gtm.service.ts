import { Injectable } from "@angular/core";
import { environment } from "../../environments/environment";
import { COOKIE_KEY } from "../libs/Constants";
import { Responsable } from "../main/model/Responsable";
import { LoginService } from "../main/services/login.service";
import { BasePayload, CtaClickPayload, FormSubmitPayload, PageViewPayload } from "./GtmPayload";
import { PlatformService } from "./platform.service";

export enum GtmEventName {
  PageView = 'page_view',
  CtaClick = 'cta_click',
  FormSubmit = 'form_submit',
  BeginCheckout = 'begin_checkout',
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
    private loginService: LoginService
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

    this.isInitialized = true;
  }

  private push(event: GtmEventName, payload: BasePayload) {
    const win = this.platformService.getNativeWindow()! as any;
    if (!this.platformService.isBrowser() || !win.dataLayer) return;

    if (this.currentUser)
      payload.user = { id: this.currentUser.id };
    payload.consent = localStorage.getItem(COOKIE_KEY) == "true";

    if (payload && payload.page)
      payload.page.website = "media";

    const eventData = { event, ...payload };
    win.dataLayer.push(eventData);

    if (!environment.production) {
      console.log('[GTM]', eventData);
    }
  }

  trackPageView(payload: PageViewPayload) {
    this.push(GtmEventName.PageView, payload);
  }

  trackCtaClick(payload: CtaClickPayload) {
    this.push(GtmEventName.CtaClick, payload);
  }

  trackFormSubmit(payload: FormSubmitPayload) {
    this.push(GtmEventName.FormSubmit, payload);
  }
}
