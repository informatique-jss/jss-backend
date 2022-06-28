import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/appRest.service';
import { SpecialOffer } from '../../miscellaneous/model/SpecialOffer';

@Injectable({
  providedIn: 'root'
})
export class SpecialOfferService extends AppRestService<SpecialOffer>{

  constructor(http: HttpClient) {
    super(http, "miscellaneous");
  }

  getSpecialOffers() {
    return this.getList(new HttpParams(), "special-offers");
  }
  
   addOrUpdateSpecialOffer(specialOffer: SpecialOffer) {
    return this.addOrUpdate(new HttpParams(), "special-offer", specialOffer, "Enregistré", "Erreur lors de l'enregistrement");
  }

}
