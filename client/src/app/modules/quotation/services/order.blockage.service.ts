import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { OrderBlockage } from '../../quotation/model/OrderBlockage';

@Injectable({
  providedIn: 'root'
})
export class OrderBlockageService extends AppRestService<OrderBlockage>{

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  getOrderBlockages() {
    return this.getList(new HttpParams(), "order-blockages");
  }
  
   addOrUpdateOrderBlockage(orderBlockage: OrderBlockage) {
    return this.addOrUpdate(new HttpParams(), "order-blockage", orderBlockage, "Enregistré", "Erreur lors de l'enregistrement");
  }

}
