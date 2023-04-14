import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { PhoneSearch } from '../../tiers/model/PhoneSearch';

@Injectable({
  providedIn: 'root'
})
export class PhoneService extends AppRestService<PhoneSearch> {

  constructor(http: HttpClient) {
    super(http, "tiers");
  }

  getPhones(phoneNumber: string) {
    return this.getList(new HttpParams().set("phoneNumber", phoneNumber), "phone/search");
  }
}
