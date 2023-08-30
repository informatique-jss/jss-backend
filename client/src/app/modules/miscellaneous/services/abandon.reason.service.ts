import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { AbandonReason } from '../../miscellaneous/model/AbandonReason';
import { Data } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class AbandonReasonService extends AppRestService<AbandonReason>{

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  getAbandonReasons() {
    return this.getListCached(new HttpParams(), "abandon-reasons");
  }

  addOrUpdateCustomerOrderAbandonReason(abandonReason: AbandonReason, id_commande: number) {
    return this.addOrUpdate(new HttpParams().set("id_commande", id_commande), "abandon-reason-customer-order", abandonReason, "Enregistré", "Erreur lors de l'enregistrement");
  }

  addOrUpdateAbandonReason(abandonReason: AbandonReason) {
    this.clearListCache(new HttpParams(), "abandon-reason");
    return this.addOrUpdate(new HttpParams(), "abandonReason", abandonReason, "Enregistré", "Erreur lors de l'enregistrement");
  }
}
