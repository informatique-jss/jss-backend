import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/appRest.service';
import { Phone } from '../../tiers/model/Phone';

@Injectable({
  providedIn: 'root'
})
export class PhoneService extends AppRestService<Phone>{

  constructor(http: HttpClient) {
    super(http, "tiers");
  }

  getPhones(phone: string) {
    return this.getList(new HttpParams().set("phone", phone), "phones/search");
  }

}
