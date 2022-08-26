import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { Department } from '../../miscellaneous/model/Department';

@Injectable({
  providedIn: 'root'
})
export class DepartmentService extends AppRestService<Department>{

  constructor(http: HttpClient) {
    super(http, "miscellaneous");
  }

  getDepartments() {
    return this.getList(new HttpParams(), "departments");
  }

  addOrUpdateDepartment(department: Department) {
    return this.addOrUpdate(new HttpParams(), "department", department, "Enregistré", "Erreur lors de l'enregistrement");
  }

}
