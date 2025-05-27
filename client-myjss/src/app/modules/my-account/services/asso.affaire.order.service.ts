import { HttpClient, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { AppRestService } from "../../main/services/appRest.service";
import { AssoAffaireOrder } from "../model/AssoAffaireOrder";
import { CustomerOrder } from "../model/CustomerOrder";
import { Quotation } from "../model/Quotation";


@Injectable({
  providedIn: 'root'
})
export class AssoAffaireOrderService extends AppRestService<AssoAffaireOrder> {

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  getAssoAffaireOrdersForCustomerOrder(customerOrder: CustomerOrder) {
    return this.getList(new HttpParams().set('idCustomerOrder', customerOrder.id), "order/asso");
  }

  getAssoAffaireOrdersForQuotation(quotation: Quotation) {
    return this.getList(new HttpParams().set('idQuotation', quotation.id), "quotation/asso");
  }

}
