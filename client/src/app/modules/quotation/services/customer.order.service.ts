import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { IQuotation } from '../model/IQuotation';

@Injectable({
  providedIn: 'root'
})
export class CustomerOrderService extends AppRestService<IQuotation>{

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  updateCustomerStatus(customerOrder: IQuotation, targetStatusCode: string) {
    return this.addOrUpdate(new HttpParams().set("targetStatusCode", targetStatusCode), "customer-order/status", customerOrder, "Commande enregistrée", "Erreur lors de la mise à jour du statut");
  }

  getCustomerOrder(idCustomerOrder: number) {
    return this.getById("customer-order", idCustomerOrder);
  }

  addOrUpdateCustomerOrder(customerOrder: IQuotation) {
    return this.addOrUpdate(new HttpParams(), "customer-order", customerOrder, "Commande enregistrée", "Erreur lors de l'enregistrement de la commande");
  }

}
