import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { SpecialOffer } from '../../miscellaneous/model/SpecialOffer';

@Injectable({
  providedIn: 'root'
})
export class SpecialOfferService extends AppRestService<SpecialOffer>{

  constructor(http: HttpClient) {
    super(http, "miscellaneous");
  }

  getSpecialOffers() {
    return this.getListCached(new HttpParams(), "special-offers");
  }

  addOrUpdateSpecialOffer(specialOffer: SpecialOffer) {
    this.clearListCache(new HttpParams(), "special-offers");
    return this.addOrUpdate(new HttpParams(), "special-offer", specialOffer, "Enregistré", "Erreur lors de l'enregistrement");
  }

}
