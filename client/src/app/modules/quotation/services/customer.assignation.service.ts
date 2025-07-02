import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { AppRestService } from 'src/app/services/appRest.service';
import { CustomerOrderAssignation } from '../model/CustomerOrderAssignation';

@Injectable({
  providedIn: 'root'
})
export class CustomerOrderAssignationService extends AppRestService<CustomerOrderAssignation> {

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  updateCustomerOrderAssignation(idCustomerOrderAssignation: number, idEmployee: number | undefined) {
    let params = new HttpParams().set("idCustomerOrderAssignation", idCustomerOrderAssignation);
    if (idEmployee)
      params = params.set("idEmployee", idEmployee);
    return this.get(params, "customer-order-assignation/update", "Assignation mise à jour");
  }

  assignImmediatlyOrder(idCustomerOrder: number) {
    return this.get(new HttpParams().set("idCustomerOrder", idCustomerOrder), "customer-order-assignation/assign/immediatly");
  }

  getNextPriorityOrderForFond() {
    return this.get(new HttpParams(), "assign/new/fond/priority") as any as Observable<number>;
  }

  getNextPriorityOrderForCommon(complexity: number) {
    return this.get(new HttpParams().set("complexity", complexity), "assign/new/common/priority") as any as Observable<number>;
  }

  getNextOrderForFond(complexity: number) {
    return this.get(new HttpParams().set("complexity", complexity), "assign/new/fond") as any as Observable<number>;
  }

  getNextOrderForCommon(complexity: number) {
    return this.get(new HttpParams().set("complexity", complexity), "assign/new/common") as any as Observable<number>;
  }

  getFondTypeToUse(complexity: number) {
    return this.get(new HttpParams().set("complexity", complexity), "assign/fond/type") as any as Observable<any>;
  }
}
