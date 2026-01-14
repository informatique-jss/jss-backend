import { HttpClient, HttpContext, HttpParams } from '@angular/common/http';
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
export class Ga4Service {

  entryPoint: String = "miscellaneous";

  constructor(protected _http: HttpClient, private platformService: PlatformService) {
  }

  trackViewItemList(serviceFamily: ServiceFamily, affaire?: Affaire) {
    let params = new HttpParams();
    params = params.set("serviceFamilyId", serviceFamily.id);
    if (affaire && affaire.id)
      params = params.set("affaireId", affaire.id);
    return this.postList(params, "ga4/view-list-item", serviceFamily.services);
  }

  trackAddToCart(service: ServiceType, affaire?: Affaire) {
    let params = new HttpParams();
    if (affaire && affaire.id)
      params = params.set("affaireId", affaire.id);
    return this.postList(params, "ga4/add-to-cart", service);
  }

  trackRemoveFromCart(service: ServiceType, affaire?: Affaire) {
    let params = new HttpParams();
    if (affaire && affaire.id)
      params = params.set("affaireId", affaire.id);
    return this.postList(params, "ga4/remove-from-cart", service);
  }

  trackBeginCheckoutQuotation(quotation: Quotation) {
    return this.postList(new HttpParams(), "ga4/begin-checkout/quotation", quotation);
  }

  trackBeginCheckoutCustomerOrder(customerOrder: CustomerOrder) {
    return this.postList(new HttpParams(), "ga4/begin-checkout/customer-order", customerOrder);
  }

  trackAddPaymentInfoQuotation(quotation: Quotation) {
    return this.postList(new HttpParams(), "ga4/add_payment_info/quotation", quotation);
  }

  trackAddPaymentInfoCustomerOrder(customerOrder: CustomerOrder) {
    return this.postList(new HttpParams(), "ga4/add_payment_info/customer-order", customerOrder);
  }

  private postList(params: HttpParams, api: string, item?: any) {
    let context: HttpContext = new HttpContext();
    return this._http.post(AppRestService.serverUrl + this.entryPoint + "/" + api, item, { params, context });
  }
}
