import { Observable } from 'rxjs';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/appRest.service';
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

  getSalesEmployees(): Observable<Employee[]> {
    return this.getList(new HttpParams(), "employee/sales");
  }

  getFormalisteEmployees(): Observable<Employee[]> {
    return this.getList(new HttpParams(), "employee/formalistes");
  }

  getInsetionEmployees(): Observable<Employee[]> {
    return this.getList(new HttpParams(), "employee/insertions");
  }

}
