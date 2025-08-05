import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { AppRestService } from '../../main/services/appRest.service';
import { Employee } from '../model/Employee';
import { Responsable } from '../model/Responsable';

@Injectable({
  providedIn: 'root'
})
export class EmployeeService extends AppRestService<Employee> {

  constructor(http: HttpClient) {
    super(http, "profile");
  }

  getEmployees(): Observable<Employee[]> {
    return this.getList(new HttpParams(), "employee/all");
  }

  getCurrentEmployee() {
    return this.get(new HttpParams(), "user");
  }

  addOrUpdateEmployee(employee: Employee) {
    this.clearListCache(new HttpParams(), "employee/all");
    return this.addOrUpdate(new HttpParams(), "employee", employee, "Profil mis à jour" + ((employee.backups && employee.backups.length > 0) ? ". Bonnes vacances ! 😎" : ""), "Erreur lors de la mise à jour du profil");
  }

  getEmployeeBackgoundColor(employee: Employee | Responsable | undefined): string {
    if (employee && employee.firstname) {
      const name = employee.firstname + employee.lastname;
      var hash = 0;
      for (var i = 0; i < name.length; i++) {
        hash = name.charCodeAt(i) + ((hash << 5) - hash);
      }
      var colour = '#';
      for (var i = 0; i < 3; i++) {
        var value = (hash >> (i * 8)) & 0xFF;
        colour += ('00' + value.toString(16)).substr(-2);
      }
      return colour;
    }
    return "#973434";
  }

}
