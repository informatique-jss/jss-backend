import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
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

  addOrUpdateProvisionType(provisionType: ProvisionType) {
    return this.addOrUpdate(new HttpParams(), "provision-type", provisionType, "Enregistré", "Erreur lors de l'enregistrement");
  }

}
