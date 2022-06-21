import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/appRest.service';
import { DeliveryService } from '../model/DeliveryService';

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
