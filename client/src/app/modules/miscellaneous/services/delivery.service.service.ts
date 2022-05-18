import { DeliveryService } from './../../../../../../.history/client/src/app/modules/miscellaneous/model/DeliveryService_20220517174004';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/appRest.service';
import { Civility } from '../../miscellaneous/model/Civility';

@Injectable({
  providedIn: 'root'
})
export class DeliveryServiceService extends AppRestService<DeliveryService>{

  constructor(http: HttpClient) {
    super(http, "tiers");
  }

  getDeliveryServices() {
    return this.getList(new HttpParams(), "delivery-services");
  }

}
