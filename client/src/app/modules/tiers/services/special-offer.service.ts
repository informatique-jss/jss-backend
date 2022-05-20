import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/appRest.service';
import { SpecialOffer } from '../../tiers/model/SpecialOffer';

@Injectable({
  providedIn: 'root'
})
export class SpecialOfferService extends AppRestService<SpecialOffer>{

  constructor(http: HttpClient) {
    super(http, "tiers");
  }

  getSpecialOffers() {
    return this.getList(new HttpParams(), "special-offers");
  }

}
