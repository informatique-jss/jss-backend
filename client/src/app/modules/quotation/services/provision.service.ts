import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/appRest.service';
import { Provision } from '../../quotation/model/Provision';

@Injectable({
  providedIn: 'root'
})
export class ProvisionService extends AppRestService<Provision>{

  constructor(http: HttpClient) {
    super(http, "quotation");
  }
}
