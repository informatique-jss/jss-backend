import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { AppRestService } from 'src/app/services/appRest.service';
import { Employee } from '../model/Employee';

@Injectable({
  providedIn: 'root'
})
export class EmployeeService extends AppRestService<Employee>{

  constructor(http: HttpClient) {
    super(http, "profile");
  }

  getEmployee(id: number): Observable<Employee> {
    return this.getById("employee", id);
  }

  getEmployees(): Observable<Employee[]> {
    return this.getList(new HttpParams(), "employee/all");
  }

}
