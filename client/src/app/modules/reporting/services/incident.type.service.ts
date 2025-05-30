import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { IncidentType } from '../../reporting/model/IncidentType';

@Injectable({
  providedIn: 'root'
})
export class IncidentTypeService extends AppRestService<IncidentType>{

  constructor(http: HttpClient) {
    super(http, "reporting");
  }

  getIncidentTypes() {
    return this.getList(new HttpParams(), "incident-types");
  }
  
   addOrUpdateIncidentType(incidentType: IncidentType) {
    return this.addOrUpdate(new HttpParams(), "incident-type", incidentType, "Enregistré", "Erreur lors de l'enregistrement");
  }

}
