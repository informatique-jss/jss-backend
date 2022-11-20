import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { CompetentAuthorityType } from '../../miscellaneous/model/CompetentAuthorityType';

@Injectable({
  providedIn: 'root'
})
export class CompetentAuthorityTypeService extends AppRestService<CompetentAuthorityType>{

  constructor(http: HttpClient) {
    super(http, "miscellaneous");
  }

  getCompetentAuthorityTypes() {
    return this.getListCached(new HttpParams(), "competent-authority-types");
  }

  addOrUpdateCompetentAuthorityType(competentAuthorityType: CompetentAuthorityType) {
    this.clearListCache(new HttpParams(), "competent-authority-types");
    return this.addOrUpdate(new HttpParams(), "competent-authority-type", competentAuthorityType, "Enregistré", "Erreur lors de l'enregistrement");
  }

}
