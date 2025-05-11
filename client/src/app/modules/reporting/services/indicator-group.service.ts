import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { IndicatorGroup } from '../../reporting/model/IndicatorGroup';

@Injectable({
  providedIn: 'root'
})
export class IndicatorGroupService extends AppRestService<IndicatorGroup> {

  constructor(http: HttpClient) {
    super(http, "indicator");
  }

  getIndicatorGroups() {
    return this.getList(new HttpParams(), "indicator-groups");
  }

  addOrUpdateIndicatorGroup(indicatorGroup: IndicatorGroup) {
    return this.addOrUpdate(new HttpParams(), "indicator-group", indicatorGroup, "Enregistré", "Erreur lors de l'enregistrement");
  }

}
