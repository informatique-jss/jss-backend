import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { PhoneTeams } from '../../tiers/model/PhoneTeams';

@Injectable({
  providedIn: 'root'
})
export class PhoneService extends AppRestService<PhoneTeams[]> {

  constructor(http: HttpClient) {
    super(http, "tiers");
  }

  getPhones(phoneNumber: string) {
    const params = new HttpParams()
    .set("phoneNumber", phoneNumber);
    return this.getList(params, `phone/${phoneNumber}`);
  }
}
