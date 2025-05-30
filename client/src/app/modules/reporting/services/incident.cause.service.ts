import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { IncidentCause } from '../../reporting/model/IncidentCause';

@Injectable({
  providedIn: 'root'
})
export class IncidentCauseService extends AppRestService<IncidentCause>{

  constructor(http: HttpClient) {
    super(http, "reporting");
  }

  getIncidentCauses() {
    return this.getList(new HttpParams(), "incident-causes");
  }
  
   addOrUpdateIncidentCause(incidentCause: IncidentCause) {
    return this.addOrUpdate(new HttpParams(), "incident-cause", incidentCause, "Enregistré", "Erreur lors de l'enregistrement");
  }

}
