import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/appRest.service';
import { Gift } from '../../miscellaneous/model/Gift';

@Injectable({
  providedIn: 'root'
})
export class GiftService extends AppRestService<Gift>{

  constructor(http: HttpClient) {
    super(http, "tiers");
  }

  getGifts() {
    return this.getList(new HttpParams(), "gifts");
  }

}
