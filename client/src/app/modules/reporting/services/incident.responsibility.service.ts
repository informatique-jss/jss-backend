import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { IncidentResponsibility } from '../../reporting/model/IncidentResponsibility';

@Injectable({
  providedIn: 'root'
})
export class IncidentResponsibilityService extends AppRestService<IncidentResponsibility>{

  constructor(http: HttpClient) {
    super(http, "reporting");
  }

  getIncidentResponsibilities() {
    return this.getList(new HttpParams(), "incident-responsibilities");
  }
  
   addOrUpdateIncidentResponsibility(incidentResponsibility: IncidentResponsibility) {
    return this.addOrUpdate(new HttpParams(), "incident-responsibility", incidentResponsibility, "Enregistré", "Erreur lors de l'enregistrement");
  }

}
