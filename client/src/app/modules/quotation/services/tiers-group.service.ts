import { HttpClient, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { AppRestService } from "src/app/services/appRest.service";
import { TiersGroup } from "../../tiers/model/TiersGroup";

@Injectable({
  providedIn: 'root'
})
export class TiersGroupService extends AppRestService<TiersGroup> {
  constructor(http: HttpClient) {
    super(http, "tiers");
  }

  getTiersGroups() {
    return this.getListCached(new HttpParams(), "tiers-groups");
  }

  addOrUpdateTiersGroup(tiersGroup: TiersGroup) {
    this.clearListCache(new HttpParams(), "tiers-groups");
    return this.addOrUpdate(new HttpParams(), "tiers-group", tiersGroup, "Enregistré", "Erreur lors de l'enregistrement");
  }
}
