import { HttpClient, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { AppRestService } from "src/app/services/appRest.service";
import { KpiCrm } from "../../crm/model/KpiCrm";

@Injectable({
  providedIn: 'root'
})
export class KpiCrmService extends AppRestService<KpiCrm> {

  constructor(http: HttpClient) {
    super(http, "miscellaneous");
  }

  getKpiCrms() {
    return this.getListCached(new HttpParams(), "kpis-crm");
  }
}
