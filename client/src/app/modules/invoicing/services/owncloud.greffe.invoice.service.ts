import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { CustomerOrder } from '../../quotation/model/CustomerOrder';
import { OwncloudGreffeInvoice } from '../../quotation/model/OwncloudGreffeInvoice';
import { Provision } from '../../quotation/model/Provision';

@Injectable({
  providedIn: 'root'
})
export class OwncloudGreffeInvoiceService extends AppRestService<OwncloudGreffeInvoice>{

  constructor(http: HttpClient) {
    super(http, "invoicing");
  }

  getOwncloudGreffeInvoiceByCustomerOrder(customerOrder: CustomerOrder) {
    return this.getList(new HttpParams().set("customerOrderId", customerOrder.id), "greffe/invoice/search/order")
  }

  getOwncloudGreffeInvoiceByNumero(numero: string) {
    return this.getList(new HttpParams().set("numero", numero), "greffe/invoice/search/numero")
  }

  createInvoiceFromGreffeInvoice(greffeInvoice: OwncloudGreffeInvoice, provision: Provision) {
    return this.get(new HttpParams().set("owncloudGreffeInvoiceId", greffeInvoice.id).set("provisionId", provision.id), "greffe/invoice/create");
  }
}
