import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { Employee } from '../../profile/model/Employee';
import { CustomerOrder } from '../../quotation/model/CustomerOrder';
import { AffectationEmployee } from '../model/AffectationEmployee';

@Injectable({
  providedIn: 'root'
})
export class AffectationEmployeeService extends AppRestService<AffectationEmployee<CustomerOrder>> {

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  findTeamEmployee(employee: Employee) {
    return this.getList(new HttpParams().append("idEmployee", employee.id), "team/employees");
  }
}
