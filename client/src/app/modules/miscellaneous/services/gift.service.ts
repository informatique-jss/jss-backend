import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { Gift } from '../../miscellaneous/model/Gift';

@Injectable({
  providedIn: 'root'
})
export class GiftService extends AppRestService<Gift>{

  constructor(http: HttpClient) {
    super(http, "miscellaneous");
  }

  getGifts() {
    return this.getListCached(new HttpParams(), "gifts");
  }

  addOrUpdateGift(gift: Gift) {
    this.clearListCache(new HttpParams(), "gifts");
    return this.addOrUpdate(new HttpParams(), "gift", gift, "Enregistré", "Erreur lors de l'enregistrement");
  }

}
