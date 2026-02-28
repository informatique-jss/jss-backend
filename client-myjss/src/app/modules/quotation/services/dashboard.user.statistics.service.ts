import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from '../../main/services/appRest.service';
import { Responsable } from '../../profile/model/Responsable';
import { DashboardUserStatistics } from '../model/DashboardUserStatistics';

@Injectable({
  providedIn: 'root'
})
export class DashboardUserStatisticsService extends AppRestService<DashboardUserStatistics> {

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  getDashboardUserStatistics(filteredResponsables: Responsable[] | undefined) {
    let params = new HttpParams();
    if (filteredResponsables && filteredResponsables.length > 0)
      params = params.set("filteredResponsableIds", filteredResponsables.map(r => r.id).join(","));
    return this.get(params, "dashboard/user/statistics");
  }
}
