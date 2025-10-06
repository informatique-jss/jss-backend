import { HttpClient, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { KpiWidget } from "../../main/model/KpiWidget";
import { AppRestService } from "../../main/services/appRest.service";
import { Responsable } from "../../profile/model/Responsable";

@Injectable({
  providedIn: 'root'
})
export class KpiWidgetService extends AppRestService<KpiWidget> {
  constructor(http: HttpClient) {
    super(http, "tiers");
  }

  getKpiWidgetsByPage(displayedPageCode: string, timeScale: string, responsables: Responsable[]) {
    return this.getList(new HttpParams().set("displayedPageCode", displayedPageCode).set("timeScale", timeScale).set("responsableIds", responsables.map(s => s.id).join(",")), "kpi-widgets");
  }

  getKpiWidgetSerieValues(kpi: KpiWidget, responsables: Responsable[]) {
    return this.getList(new HttpParams().set("displayedPageCode", kpi.idKpi).set("responsableIds", responsables.map(s => s.id).join(",")), "kpi-values");
  }

}
