import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { BusinessSector } from '../../tiers/model/BusinessSector';

@Injectable({
  providedIn: 'root'
})
export class BusinessSectorService extends AppRestService<BusinessSector>{

  constructor(http: HttpClient) {
    super(http, "tiers");
  }

  getBusinessSectors() {
    return this.getList(new HttpParams(), "business-sectors");
  }
  
   addOrUpdateBusinessSector(businessSector: BusinessSector) {
    return this.addOrUpdate(new HttpParams(), "business-sector", businessSector, "Enregistré", "Erreur lors de l'enregistrement");
  }

}
