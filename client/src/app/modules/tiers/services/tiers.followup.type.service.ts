import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { TiersFollowupType } from '../../tiers/model/TiersFollowupType';

@Injectable({
  providedIn: 'root'
})
export class TiersFollowupTypeService extends AppRestService<TiersFollowupType>{

  constructor(http: HttpClient) {
    super(http, "tiers");
  }

  getTiersFollowupTypes() {
    return this.getListCached(new HttpParams(), "tiers-followup-types");
  }

  addOrUpdateTiersFollowupType(tiersFollowupType: TiersFollowupType) {
    this.clearListCache(new HttpParams(), "tiers-followup-types");
    return this.addOrUpdate(new HttpParams(), "tiers-followup-type", tiersFollowupType, "Enregistré", "Erreur lors de l'enregistrement");
  }

}
