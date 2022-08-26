import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { CompetentAuthority } from '../../miscellaneous/model/CompetentAuthority';
import { City } from '../model/City';
import { Department } from '../model/Department';

@Injectable({
  providedIn: 'root'
})
export class CompetentAuthorityService extends AppRestService<CompetentAuthority>{

  constructor(http: HttpClient) {
    super(http, "miscellaneous");
  }

  getCompetentAuthorities() {
    return this.getList(new HttpParams(), "competent-authorities");
  }

  addOrUpdateCompetentAuthority(competentAuthority: CompetentAuthority) {
    return this.addOrUpdate(new HttpParams(), "competent-authority", competentAuthority, "Enregistré", "Erreur lors de l'enregistrement");
  }

  getCompetentAuthorityByDepartmentAndName(value: string, department: Department | undefined) {
    if (department != undefined && department != null)
      return this.getList(new HttpParams().set("departmentId", department.id!).set("authority", value), "competent-authorities/search/department");
    return this.getList(new HttpParams().set("authority", value), "competent-authorities/search/department");
  }

  getCompetentAuthorityByCity(city: City) {
    return this.getList(new HttpParams().set("cityId", city.id!), "competent-authorities/search/city");

  }

}
