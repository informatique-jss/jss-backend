import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { Employee } from '../../profile/model/Employee';
import { Provision } from '../../quotation/model/Provision';

@Injectable({
  providedIn: 'root'
})
export class ProvisionService extends AppRestService<Provision> {

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  updateAssignedToForProvision(provision: Provision, employee: Employee) {
    return this.getList(new HttpParams().set("provisionId", provision.id).set("employeeId", employee.id), "provision/assignedTo");
  }
  getRegistrationActPdf(idProvision: number) {
    this.downloadGet(new HttpParams().set("idProvision", idProvision), "provision/generate/registration-act", "Enregistrement d'acte généré", "Erreur lors du téléchargement");
  }

  downloadTrackingSheet(idProvision: number) {
    this.downloadGet(new HttpParams().set("idProvision", idProvision), "provision/generate/tracking-sheet", "Fiche de suivi générée", "Erreur lors du téléchargement");
  }
}
