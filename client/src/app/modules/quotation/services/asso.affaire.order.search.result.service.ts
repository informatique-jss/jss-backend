import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { AffaireSearch } from '../model/AffaireSearch';
import { AssoAffaireOrderSearchResult } from '../model/AssoAffaireOrderSearchResult';

@Injectable({
  providedIn: 'root'
})
export class AssoAffaireOrderSearchResultService extends AppRestService<AssoAffaireOrderSearchResult>{

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  getAssoAffaireOrders(affaireSearch: AffaireSearch) {
    return this.postList(new HttpParams(), "asso/affaire/order/search", affaireSearch);
  }

}
