import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { ProvisionScreenType } from '../../quotation/model/ProvisionScreenType';

@Injectable({
  providedIn: 'root'
})
export class ProvisionScreenTypeService extends AppRestService<ProvisionScreenType>{

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  getProvisionScreenTypes() {
    return this.getList(new HttpParams(), "provision-screen-types");
  }
  
   addOrUpdateProvisionScreenType(provisionScreenType: ProvisionScreenType) {
    return this.addOrUpdate(new HttpParams(), "provision-screen-type", provisionScreenType, "Enregistré", "Erreur lors de l'enregistrement");
  }

}
