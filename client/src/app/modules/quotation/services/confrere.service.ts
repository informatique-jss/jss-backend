import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { Department } from '../../miscellaneous/model/Department';
import { Confrere } from '../../quotation/model/Confrere';

@Injectable({
  providedIn: 'root'
})
export class ConfrereService extends AppRestService<Confrere>{

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  getConfreres() {
    return this.getList(new HttpParams(), "confreres");
  }

  getConfrereById(id: number) {
    return this.getById("confrere", id);
  }

  getConfrereFilteredByDepartmentAndName(department: Department | undefined, label: string) {
    let params = new HttpParams();
    if (department)
      params = params.set("departmentId", (department ? department.id : null) + "");
    return this.getList(params.set("label", label), "confreres/search");
  }

  addOrUpdateConfrere(confrere: Confrere) {
    this.clearListCache(new HttpParams(), "confreres");
    return this.addOrUpdate(new HttpParams(), "confrere", confrere, "Enregistré", "Erreur lors de l'enregistrement");
  }

}
