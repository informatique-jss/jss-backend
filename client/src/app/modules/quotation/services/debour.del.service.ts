import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { DebourDel } from '../model/DebourDel';
import { Provision } from '../model/Provision';

@Injectable({
  providedIn: 'root'
})
export class DebourDelService extends AppRestService<DebourDel>{

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  getDebourByProvision(provision: Provision) {
    return this.getList(new HttpParams().set("idProvision", provision.id), "debour");
  }
}
