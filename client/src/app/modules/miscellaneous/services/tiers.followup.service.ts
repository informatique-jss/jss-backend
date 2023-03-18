import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { Confrere } from '../../quotation/model/Confrere';
import { Invoice } from '../../quotation/model/Invoice';
import { Responsable } from '../../tiers/model/Responsable';
import { Tiers } from '../../tiers/model/Tiers';
import { TiersFollowup } from '../model/TiersFollowup';

@Injectable({
  providedIn: 'root'
})
export class TiersFollowupService extends AppRestService<TiersFollowup>{

  constructor(http: HttpClient) {
    super(http, "tiers");
  }

  addFollowupForTiers(tiersFollowup: TiersFollowup, tiers: Tiers) {
    return this.postList(new HttpParams().set("idTiers", tiers.id), "tiers-followup/tiers", tiersFollowup);
  }

  addFollowupForResponsable(tiersFollowup: TiersFollowup, responsable: Responsable) {
    return this.postList(new HttpParams().set("idResponsable", responsable.id), "tiers-followup/responsable", tiersFollowup);
  }

  addFollowupForConfrere(tiersFollowup: TiersFollowup, confrere: Confrere) {
    return this.postList(new HttpParams().set("idConfrere", confrere.id), "tiers-followup/confrere", tiersFollowup);
  }

  addFollowupForInvoice(tiersFollowup: TiersFollowup, invoice: Invoice) {
    return this.postList(new HttpParams().set("idInvoice", invoice.id), "tiers-followup/invoice", tiersFollowup);
  }

}
