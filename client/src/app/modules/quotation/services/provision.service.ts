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

  deleteProvision(provision: Provision) {
    return this.get(new HttpParams().set("provisionId", provision.id), "provision/delete", "Prestation supprimée", "Erreur lors de la suppression");
  }

  getRegistrationActPdf(idProvision: number) {
    this.downloadGet(new HttpParams().set("idProvision", idProvision), "provision/generate/registration-act", "Enregistrement d'acte généré", "Erreur lors du téléchargement");
  }

  downloadTrackingSheet(idProvision: number) {
    this.downloadGet(new HttpParams().set("idProvision", idProvision), "provision/generate/tracking-sheet", "Fiche de suivi générée", "Erreur lors du téléchargement");
  }

  searchProvisions(formalisteIds: number[], statusIds: number[]) {
    return this.getList(new HttpParams().set("formalisteIds", formalisteIds.join(",")).set("statusIds", statusIds.join(',')), 'provision/search');
  }

  getSingleProvision(idProvision: number) {
    return this.getById("provision/single", idProvision);
  }

  updateProvisionStatus(provisionId: number, targetStatusCode: string) {
    return this.get(new HttpParams().set("targetStatusCode", targetStatusCode).set("provisionId", provisionId), "provision/status");
  }

}
