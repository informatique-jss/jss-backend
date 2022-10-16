import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { AssignationType } from '../../quotation/model/AssignationType';

@Injectable({
  providedIn: 'root'
})
export class AssignationTypeService extends AppRestService<AssignationType>{

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  getAssignationTypes() {
    return this.getList(new HttpParams(), "assignation-types");
  }
}
