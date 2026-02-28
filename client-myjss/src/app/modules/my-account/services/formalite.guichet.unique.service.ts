import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { AppRestService } from '../../main/services/appRest.service';
import { FormaliteGuichetUnique } from '../model/FormaliteGuichetUnique';
import { GuichetUniqueDepositInfoDto } from '../model/GuichetUniqueDepositInfoDto';

@Injectable({
  providedIn: 'root'
})
export class FormaliteGuichetUniqueService extends AppRestService<FormaliteGuichetUnique> {
  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  getGuichetUniqueDatesDtosForServices(serviceId: number) {
    return this.getList(new HttpParams().set("serviceId", serviceId), "formalite-guichet-unique/dates-dtos") as any as Observable<GuichetUniqueDepositInfoDto[]>;
  }
}
