import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/appRest.service';
import { ProvisionType } from '../../quotation/model/ProvisionType';

@Injectable({
  providedIn: 'root'
})
export class ProvisionTypeService extends AppRestService<ProvisionType>{

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  getProvisionTypes() {
    return this.getList(new HttpParams(), "provision-types");
  }

}
