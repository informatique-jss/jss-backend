import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/appRest.service';
import { DeliveryService } from '../../miscellaneous/model/DeliveryService';

@Injectable({
  providedIn: 'root'
})
export class DeliveryServiceService extends AppRestService<DeliveryService>{

  constructor(http: HttpClient) {
    super(http, "miscellaneous");
  }

  getDeliveryServices() {
    return this.getList(new HttpParams(), "delivery-services");
  }
  
   addOrUpdateDeliveryService(deliveryService: DeliveryService) {
    return this.addOrUpdate(new HttpParams(), "delivery-service", deliveryService, "Enregistré", "Erreur lors de l'enregistrement");
  }

}
