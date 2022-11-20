import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { TiersType } from '../../tiers/model/TiersType';

@Injectable({
  providedIn: 'root'
})
export class TiersTypeService extends AppRestService<TiersType>{

  constructor(http: HttpClient) {
    super(http, "tiers");
  }

  getTiersTypes() {
    return this.getListCached(new HttpParams(), "tiers-types");
  }

  addOrUpdateTiersType(tiersType: TiersType) {
    this.clearListCache(new HttpParams(), "tiers-types");
    return this.addOrUpdate(new HttpParams(), "tiers-type", tiersType, "Enregistré", "Erreur lors de l'enregistrement");
  }

}
