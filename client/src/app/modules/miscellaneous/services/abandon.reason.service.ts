import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { IAbandonReason } from '../../miscellaneous/model/AbandonReason';

@Injectable({
  providedIn: 'root'
})
export class AbandonReasonService extends AppRestService<IAbandonReason>{

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  getAbandonReasons() {
    return this.getListCached(new HttpParams(), "abandon-reason");
  }

  addOrUpdateAbandonReason(abandonReason: IAbandonReason) {
    this.clearListCache(new HttpParams(), "abandon");
    return this.addOrUpdate(new HttpParams(), "abandon", abandonReason, "Enregistré", "Erreur lors de l'enregistrement");
  }

}
