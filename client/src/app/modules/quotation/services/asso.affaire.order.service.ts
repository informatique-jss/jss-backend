import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { AppRestService } from 'src/app/services/appRest.service';
import { Employee } from '../../profile/model/Employee';
import { AssoAffaireOrder } from '../../quotation/model/AssoAffaireOrder';

@Injectable({
  providedIn: 'root'
})
export class AssoAffaireOrderService extends AppRestService<AssoAffaireOrder>{

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  updateAssignedToForAsso(asso: AssoAffaireOrder, employee: Employee) {
    return this.getList(new HttpParams().set("assoId", asso.id).set("employeeId", employee.id), "asso/affaire/order/assignedTo");
  }

  getAssoAffaireOrder(idAssoAffaireOrder: number) {
    return this.getById("asso/affaire/order", idAssoAffaireOrder);
  }

  getAssoAffaireOrderFromProvision(idProvision: number) {
    return this.getById("asso/affaire/order/provision", idProvision);
  }

  updateAsso(asso: AssoAffaireOrder): Observable<AssoAffaireOrder> {
    return this.postItem(new HttpParams(), "asso/affaire/order/update", asso, "Prestations mises à jour", "Erreur lors de la mise à jour de l'affaire / prestations");
  }

}
