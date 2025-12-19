import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { CompanySize } from '../../tiers/model/CompanySize';

@Injectable({
  providedIn: 'root'
})
export class CompanySizeService extends AppRestService<CompanySize>{

  constructor(http: HttpClient) {
    super(http, "tiers");
  }

  getCompanySizes() {
    return this.getList(new HttpParams(), "company-sizes");
  }
  
   addOrUpdateCompanySize(companySize: CompanySize) {
    return this.addOrUpdate(new HttpParams(), "company-size", companySize, "Enregistré", "Erreur lors de l'enregistrement");
  }

}
