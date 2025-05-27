import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from '../../main/services/appRest.service';
import { MyJssImage } from '../model/MyJssImage';

@Injectable({
  providedIn: 'root'
})
export class MyJssImageService extends AppRestService<MyJssImage> {
  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  downloadQrCode(customerOrderId: number, mail: string) {
    return this.get(new HttpParams().set("customerOrderId", customerOrderId + "").set("mail", mail), "order/payment/cb/qrcode");
  }
}
