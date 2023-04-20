import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { SimpleProvision } from '../../quotation/model/SimpleProvision';

@Injectable({
  providedIn: 'root'
})
export class SimpleProvisionService extends AppRestService<SimpleProvision>{

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

}
