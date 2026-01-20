import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
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
export class GoogleAnalyticsService {

  entryPoint: String = "miscellaneous";

  constructor(protected _http: HttpClient, private platformService: PlatformService) {
  }

  trackViewItemList(serviceFamily: ServiceFamily, affaire?: Affaire) {
    let params = new HttpParams();
    params = params.set("serviceFamilyId", serviceFamily.id);
    if (affaire && affaire.id)
      params = params.set("affaireId", affaire.id);
    return this.postListWithGaClient(params, "google-analytics/view-list-item", serviceFamily.services);
  }

  trackAddToCart(service: ServiceType, affaire?: Affaire) {
    let params = new HttpParams();
    if (affaire && affaire.id)
      params = params.set("affaireId", affaire.id);
    return this.postListWithGaClient(params, "google-analytics/add-to-cart", service);
  }

  trackRemoveFromCart(service: ServiceType, affaire?: Affaire) {
    let params = new HttpParams();
    if (affaire && affaire.id)
      params = params.set("affaireId", affaire.id);
    return this.postListWithGaClient(params, "google-analytics/remove-from-cart", service);
  }

  trackBeginCheckoutQuotation(quotation: Quotation) {
    return this.postListWithGaClient(new HttpParams(), "google-analytics/begin-checkout/quotation", quotation);
  }

  trackBeginCheckoutCustomerOrder(customerOrder: CustomerOrder) {
    return this.postListWithGaClient(new HttpParams(), "google-analytics/begin-checkout/customer-order", customerOrder);
  }

  trackAddPaymentInfoQuotation(quotation: Quotation) {
    return this.postListWithGaClient(new HttpParams(), "google-analytics/add-payment-info/quotation", quotation);
  }

  trackAddPaymentInfoCustomerOrder(customerOrder: CustomerOrder) {
    return this.postListWithGaClient(new HttpParams(), "google-analytics/add-payment-info/customer-order", customerOrder);
  }

  private postListWithGaClient(params: HttpParams, api: string, item?: any) {
    let headers = new HttpHeaders();
    headers = headers.set('gaClientId', this.getGaClientId());
    return this._http.post(AppRestService.serverUrl + this.entryPoint + "/" + api, item, { params, headers });
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
