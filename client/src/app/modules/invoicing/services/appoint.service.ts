import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { Appoint } from '../../invoicing/model/Appoint';

@Injectable({
  providedIn: 'root'
})
export class AppointService extends AppRestService<Appoint>{

  constructor(http: HttpClient) {
    super(http, "invoicing");
  }

  getAppoints(searchLabel: string) {
    return this.getList(new HttpParams().set("searchLabel", searchLabel), "appoints");
  }

  addOrUpdateAppoint(appoint: Appoint) {
    return this.addOrUpdate(new HttpParams(), "appoint", appoint, "Enregistré", "Erreur lors de l'enregistrement");
  }


  refundAppoint(appoint: Appoint) {
    return this.get(new HttpParams().set("idAppoint", appoint.id), "appoint/refund", "Remboursement généré");
  }

}
