import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { InvoiceItem } from '../model/InvoiceItem';

@Injectable({
  providedIn: 'root'
})
export class InvoiceItemService extends AppRestService<InvoiceItem>{

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

}
