import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { Confrere } from '../../quotation/model/Confrere';
import { CustomerOrder } from '../../quotation/model/CustomerOrder';
import { Quotation } from '../../quotation/model/Quotation';
import { Responsable } from '../../tiers/model/Responsable';
import { Tiers } from '../../tiers/model/Tiers';
import { CustomerMail } from '../model/CustomerMail';

@Injectable({
  providedIn: 'root'
})
export class CustomerMailService extends AppRestService<CustomerMail>{

  constructor(http: HttpClient) {
    super(http, "miscellaneous");
  }

  getCustomerMailByQuotation(quotation: Quotation) {
    return this.getList(new HttpParams().set("idQuotation", quotation.id), "customer-mail/quotation");
  }

  getCustomerMailByCustomerOrder(customerOrder: CustomerOrder) {
    return this.getList(new HttpParams().set("idCustomerOrder", customerOrder.id), "customer-mail/customer-order");
  }

  getCustomerMailByTiers(tiers: Tiers) {
    return this.getList(new HttpParams().set("idTiers", tiers.id), "customer-mail/tiers");
  }

  getCustomerMailByResponsable(responsable: Responsable) {
    return this.getList(new HttpParams().set("idResponsable", responsable.id), "customer-mail/responsable");
  }

  getCustomerMailByConfrere(confrere: Confrere) {
    return this.getList(new HttpParams().set("idConfrere", confrere.id), "customer-mail/confrere");
  }

  sendCustomerMailImmediatly(customerMail: CustomerMail) {
    return this.get(new HttpParams().set("idCustomerMail", customerMail.id), "customer-mail/send/immediatly");
  }
}
