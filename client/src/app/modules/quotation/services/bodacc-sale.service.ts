import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/appRest.service';
import { BodaccSale } from '../../quotation/model/BodaccSale';

@Injectable({
  providedIn: 'root'
})
export class BodaccSaleService extends AppRestService<BodaccSale>{

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

}
