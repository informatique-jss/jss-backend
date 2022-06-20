import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/appRest.service';
import { CompetentAuthorityType } from '../../miscellaneous/model/CompetentAuthorityType';

@Injectable({
  providedIn: 'root'
})
export class CompetentAuthorityTypeService extends AppRestService<CompetentAuthorityType>{

  constructor(http: HttpClient) {
    super(http, "miscellaneous");
  }

  getCompetentAuthorityTypes() {
    return this.getList(new HttpParams(), "competent-authority-types");
  }

}
