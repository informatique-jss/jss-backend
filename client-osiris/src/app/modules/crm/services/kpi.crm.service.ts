import { HttpClient, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { AppRestService } from "../../main/services/appRest.service";
import { KpiCrm } from "../model/KpiCrm";

@Injectable({
  providedIn: 'root'
})
export class KpiCrmService extends AppRestService<KpiCrm> {
  constructor(http: HttpClient) {
    super(http, "kpi");
  }

  getKpiCrm() {
    return this.getList(new HttpParams(), "kpis-crm");
  }
}
