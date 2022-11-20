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

  getEmployees(): Observable<Employee[]> {
    return this.getListCached(new HttpParams(), "employee/all");
  }

  getCurrentEmployee() {
    return this.get(new HttpParams(), "user");
  }

  addOrUpdateEmployee(employee: Employee) {
    this.clearListCache(new HttpParams(), "employee/all");
    return this.addOrUpdate(new HttpParams(), "employee", employee, "Profil mis à jour" + ((employee.backups && employee.backups.length > 0) ? ". Bonnes vacances ! 😎" : ""), "Erreur lors de la mise à jour du profil");
  }

}
