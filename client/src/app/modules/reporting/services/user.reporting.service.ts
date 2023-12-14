import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { Employee } from '../../profile/model/Employee';
import { UserReporting } from '../../reporting/model/UserReporting';

@Injectable({
  providedIn: 'root'
})
export class UserReportingService extends AppRestService<UserReporting>{

  constructor(http: HttpClient) {
    super(http, "reporting");
  }

  getUserReportings(employee: Employee) {
    return this.getList(new HttpParams().set("employeeId", employee.id), "user-reportings");
  }

  getUserReporting(id: number) {
    return this.getById("user-reporting", id);
  }

  addOrUpdateUserReporting(userReporting: UserReporting) {
    return this.addOrUpdate(new HttpParams(), "user-reporting", userReporting, "Enregistré", "Erreur lors de l'enregistrement");
  }

  copyUserReportingToUser(userReporting: UserReporting, employee: Employee) {
    return this.get(new HttpParams().set("userReportingId", userReporting.id!).set("employeeId", employee.id), "user-reporting/copy", "Le rapport a été copié chez " + (employee.firstname + " " + employee.lastname), "Erreur lors de la copie");
  }

  deleteUserReporting(userReporting: UserReporting) {
    return this.get(new HttpParams().set("userReportingId", userReporting.id!), "user-reporting/delete");
  }

}
