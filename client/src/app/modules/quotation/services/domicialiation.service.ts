import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/appRest.service';
import { Domiciliation } from '../../quotation/model/Domiciliation';

@Injectable({
  providedIn: 'root'
})
export class DomiciliationService extends AppRestService<Domiciliation>{

  constructor(http: HttpClient) {
    super(http, "quotation");
  }
}
