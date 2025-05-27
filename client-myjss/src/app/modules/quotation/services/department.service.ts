import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from '../../main/services/appRest.service';
import { Department } from '../../profile/model/Department';

@Injectable({
  providedIn: 'root'
})
export class DepartmentService extends AppRestService<Department> {

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  getDepartments() {
    return this.getListCached(new HttpParams(), "departments");
  }


}
