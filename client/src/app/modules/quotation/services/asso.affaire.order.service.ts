import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { AppRestService } from 'src/app/services/appRest.service';
import { AssoAffaireOrder } from '../../quotation/model/AssoAffaireOrder';
import { CustomerOrder } from '../model/CustomerOrder';
import { Quotation } from '../model/Quotation';

@Injectable({
  providedIn: 'root'
})
export class AssoAffaireOrderService extends AppRestService<AssoAffaireOrder> {

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  getAssoAffaireOrder(idAssoAffaireOrder: number) {
    return this.getById("asso/affaire/order", idAssoAffaireOrder);
  }

  getAssoAffaireOrderFromProvision(idProvision: number) {
    return this.getById("asso/affaire/order/provision", idProvision);
  }

  getAssoAffaireOrderFromService(idService: number) {
    return this.getById("asso/affaire/order/service", idService);
  }

  updateAsso(asso: AssoAffaireOrder, isByPassMandatoryField: boolean): Observable<AssoAffaireOrder> {
    return this.postItem(new HttpParams().set("isByPassMandatoryField", isByPassMandatoryField), "asso/affaire/order/update", asso, "Prestations mises à jour", "Erreur lors de la mise à jour de l'affaire / prestations");
  }

  getAssoAffaireOrdersForCustomerOrder(customerOrder: CustomerOrder) {
    return this.getList(new HttpParams().set('idCustomerOrder', customerOrder.id), "order/asso");
  }

  getAssoAffaireOrdersForQuotation(quotation: Quotation) {
    return this.getList(new HttpParams().set('idQuotation', quotation.id), "quotation/asso");
  }

}
