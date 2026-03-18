import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Affaire } from '../../my-account/model/Affaire';
import { CustomerOrder } from '../../my-account/model/CustomerOrder';
import { Quotation } from '../../my-account/model/Quotation';
import { ServiceType } from '../../my-account/model/ServiceType';
import { ServiceFamily } from '../../quotation/model/ServiceFamily';
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
    return this.get(new HttpParams().set("eventName", eventName).set("pageName", pageName).set("pageType", pageType).set("gaClientId", this.getAnalyticsIds().clientId ? this.getAnalyticsIds().clientId! : "")
      .set("gaSessionId", this.getAnalyticsIds().sessionId ? this.getAnalyticsIds().sessionId! : ""), "google-analytics/login-logout");
  }

  trackViewItemList(serviceFamily: ServiceFamily, affaire?: Affaire) {
    let params = new HttpParams();
    params = params.set("serviceFamilyId", serviceFamily.id);
    if (affaire && affaire.id)
      params = params.set("affaireId", affaire.id);
    return this.postList(params.set("gaClientId", this.getAnalyticsIds().clientId ? this.getAnalyticsIds().clientId! : "")
      .set("gaSessionId", this.getAnalyticsIds().sessionId ? this.getAnalyticsIds().sessionId! : ""), "google-analytics/view-list-item", serviceFamily.services);
  }

  trackAddToCart(service: ServiceType, affaire?: Affaire) {
    let params = new HttpParams();
    if (affaire && affaire.id)
      params = params.set("affaireId", affaire.id);
    return this.postList(params.set("gaClientId", this.getAnalyticsIds().clientId ? this.getAnalyticsIds().clientId! : "")
      .set("gaSessionId", this.getAnalyticsIds().sessionId ? this.getAnalyticsIds().sessionId! : ""), "google-analytics/add-to-cart", service);
  }

  trackRemoveFromCart(service: ServiceType, affaire?: Affaire) {
    let params = new HttpParams();
    if (affaire && affaire.id)
      params = params.set("affaireId", affaire.id);
    return this.postList(params.set("gaClientId", this.getAnalyticsIds().clientId ? this.getAnalyticsIds().clientId! : "")
      .set("gaSessionId", this.getAnalyticsIds().sessionId ? this.getAnalyticsIds().sessionId! : ""), "google-analytics/remove-from-cart", service);
  }

  trackBeginCheckoutQuotation(quotation: Quotation) {
    return this.postList(new HttpParams().set("gaClientId", this.getAnalyticsIds().clientId ? this.getAnalyticsIds().clientId! : "")
      .set("gaSessionId", this.getAnalyticsIds().sessionId ? this.getAnalyticsIds().sessionId! : ""), "google-analytics/begin-checkout/quotation", quotation);
  }

  trackBeginCheckoutCustomerOrder(customerOrder: CustomerOrder) {
    return this.postList(new HttpParams().set("gaClientId", this.getAnalyticsIds().clientId ? this.getAnalyticsIds().clientId! : "")
      .set("gaSessionId", this.getAnalyticsIds().sessionId ? this.getAnalyticsIds().sessionId! : ""), "google-analytics/begin-checkout/customer-order", customerOrder);
  }

  trackAddPaymentInfoQuotation(quotation: Quotation) {
    return this.postList(new HttpParams().set("gaClientId", this.getAnalyticsIds().clientId ? this.getAnalyticsIds().clientId! : "")
      .set("gaSessionId", this.getAnalyticsIds().sessionId ? this.getAnalyticsIds().sessionId! : ""), "google-analytics/add-payment-info/quotation", quotation);
  }

  trackAddPaymentInfoCustomerOrder(customerOrder: CustomerOrder) {
    return this.postList(new HttpParams().set("gaClientId", this.getAnalyticsIds().clientId ? this.getAnalyticsIds().clientId! : "")
      .set("gaSessionId", this.getAnalyticsIds().sessionId ? this.getAnalyticsIds().sessionId! : ""), "google-analytics/add-payment-info/customer-order", customerOrder);
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
