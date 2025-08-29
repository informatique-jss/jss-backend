import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AnalyticStatsType } from '../../main/model/AnalyticStatsType';
import { AppRestService } from '../../main/services/appRest.service';
import { Responsable } from '../../profile/model/Responsable';
@Injectable({
  providedIn: 'root'
})
export class AnalyticStatsTypeService extends AppRestService<AnalyticStatsType> {

  constructor(http: HttpClient) {
    super(http, "tiers");
  }

  getAnalyticStatsTypeForTiers(codeKpi: string, responsables: Responsable[]) {
    return this.getList(new HttpParams().set("responsableIds", responsables.map(s => s.id).join(",")).set("kpiCrmCode", codeKpi), "analytic-stats-types");
  }


}
