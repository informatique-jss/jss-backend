import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { Attachment } from '../../miscellaneous/model/Attachment';
import { Provision } from '../model/Provision';

@Injectable({
  providedIn: 'root'
})
export class KbisRequestService extends AppRestService<Attachment> {

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  getUpToDateKbisForSiren(siren: string) {
    return this.get(new HttpParams().set("siren", siren), "kbis-request/siren");
  }

  requestKbisForSiren(siren: string, provision: Provision) {
    return this.get(new HttpParams().set("siren", siren).set("provisionId", provision.id), "kbis-request/order", "Le KBis est en cours de commande. Une notification sera émise lorsqu'il sera disponible sur cette prestation.");
  }
}
