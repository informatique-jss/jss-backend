import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { OfferReason } from '../model/OfferReason';

@Injectable({
  providedIn: 'root'
})
export class OfferReasonService extends AppRestService<OfferReason>{

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  getOfferReasons() {
    return this.getListCached(new HttpParams(), "offer-reasons");
  }

  addOrUpdateCustomerOrderOfferReason(offerReason: OfferReason, id_commande: number) {
    return this.addOrUpdate(new HttpParams().set("id_commande", id_commande), "offer-reason-customer-order", offerReason, "Enregistré", "Erreur lors de l'enregistrement");
  }

  addOrUpdateOfferReason(offerReason: OfferReason) {
    return this.addOrUpdate(new HttpParams(), "offer-reason", offerReason, "Enregistré", "Erreur lors de l'enregistrement");
  }
}
