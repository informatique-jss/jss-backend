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
    return this.getListCached(new HttpParams(), "provision-family-types");
  }


  addOrUpdateProvisionFamilyType(provisionFamilyType: ProvisionFamilyType) {
    this.clearListCache(new HttpParams(), "provision-family-types");
    return this.addOrUpdate(new HttpParams(), "provision-family-type", provisionFamilyType, "Enregistré", "Erreur lors de l'enregistrement");
  }

}
