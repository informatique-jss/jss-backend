import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { Rff } from '../../tiers/model/Rff';
import { RffSearch } from '../model/RffSearch';

@Injectable({
  providedIn: 'root'
})
export class RffService extends AppRestService<Rff>{

  constructor(http: HttpClient) {
    super(http, "tiers");
  }

  getRffs(rffSearch: RffSearch) {
    return this.postList(new HttpParams(), "rffs", rffSearch);
  }

  addOrUpdateRff(rff: Rff) {
    return this.addOrUpdate(new HttpParams(), "rff", rff, "Enregistré", "Erreur lors de l'enregistrement");
  }

  cancelRff(rff: Rff) {
    return this.get(new HttpParams().set("idRff", rff.id), "rff/cancel");
  }

  sendRff(rff: Rff, amount: number, sendToMe: boolean) {
    return this.get(new HttpParams().set("idRff", rff.id).set("amount", amount).set("sendToMe", sendToMe), "rff/send", "Mail envoyé !");
  }

  generateInvoiceForRff(rff: Rff) {
    return this.get(new HttpParams().set("idRff", rff.id), "rff/invoice");
  }

}
