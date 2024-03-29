import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { CompetentAuthority } from '../../miscellaneous/model/CompetentAuthority';
import { City } from '../model/City';
import { CompetentAuthorityType } from '../model/CompetentAuthorityType';
import { Department } from '../model/Department';

@Injectable({
  providedIn: 'root'
})
export class CompetentAuthorityService extends AppRestService<CompetentAuthority>{

  constructor(http: HttpClient) {
    super(http, "miscellaneous");
  }

  getCompetentAuthorities() {
    return this.getListCached(new HttpParams(), "competent-authorities");
  }

  getCompetentAuthorityById(id: number) {
    return this.getById("competent-authority", id);
  }

  addOrUpdateCompetentAuthority(competentAuthority: CompetentAuthority) {
    this.clearListCache(new HttpParams(), "competent-authorities");
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

  getCompetentAuthoritiesByType(competentAuthorityType: CompetentAuthorityType) {
    return this.getList(new HttpParams().set("competentAuthorityTypeId", competentAuthorityType.id!), "competent-authorities/search/competent-authority-type");
  }

}
