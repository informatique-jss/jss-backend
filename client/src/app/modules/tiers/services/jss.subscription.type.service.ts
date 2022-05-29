import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/appRest.service';
import { JssSubscriptionType } from '../../tiers/model/JssSubscriptionType';

@Injectable({
  providedIn: 'root'
})
export class JssSubscriptionTypeService extends AppRestService<JssSubscriptionType>{

  constructor(http: HttpClient) {
    super(http, "tiers");
  }

  getJssSubscriptionTypes() {
    return this.getList(new HttpParams(), "jss-suscription-types");
  }

}
