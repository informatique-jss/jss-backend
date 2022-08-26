import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { ProvisionFamilyType } from '../../quotation/model/ProvisionFamilyType';

@Injectable({
  providedIn: 'root'
})
export class ProvisionFamilyTypeService extends AppRestService<ProvisionFamilyType>{

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  getProvisionFamilyTypes() {
    return this.getList(new HttpParams(), "provision-family-types");
  }


  addOrUpdateProvisionFamilyType(provisionFamilyType: ProvisionFamilyType) {
    return this.addOrUpdate(new HttpParams(), "provision-family-types", provisionFamilyType, "Enregistré", "Erreur lors de l'enregistrement");
  }

}
