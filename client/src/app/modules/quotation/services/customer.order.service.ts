import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/appRest.service';
import { IQuotation } from '../model/IQuotation';

@Injectable({
  providedIn: 'root'
})
export class CustomerOrderService extends AppRestService<IQuotation>{

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  updateCustomerStatus(customerOrder: IQuotation) {
    return this.addOrUpdate(new HttpParams(), "customer-order/status", customerOrder, "Commande enregistrée", "Erreur lors de l'enregistrement de la commande");
  }

  getCustomerOrder(idCustomerOrder: number) {
    return this.getById("customer-order", idCustomerOrder);
  }

  addOrUpdateCustomerOrder(customerOrder: IQuotation) {
    return this.addOrUpdate(new HttpParams(), "customer-order", customerOrder, "Commande enregistrée", "Erreur lors de l'enregistrement de la commande");
  }

}
