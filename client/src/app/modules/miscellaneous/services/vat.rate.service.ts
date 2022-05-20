import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/appRest.service';
import { VatRate } from '../../miscellaneous/model/VatRate';

@Injectable({
  providedIn: 'root'
})
export class VatRateService extends AppRestService<VatRate>{

  constructor(http: HttpClient) {
    super(http, "miscellaneous");
  }

  getVatRates() {
    return this.getList(new HttpParams(), "vat-rates");
  }

}
