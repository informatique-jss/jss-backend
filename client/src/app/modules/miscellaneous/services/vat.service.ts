import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/appRest.service';
import { Vat } from '../model/Vat';

@Injectable({
  providedIn: 'root'
})
export class VatService extends AppRestService<Vat>{

  constructor(http: HttpClient) {
    super(http, "miscellaneous");
  }

  getVat() {
    return this.getList(new HttpParams(), "vat-rates");
  }

}
