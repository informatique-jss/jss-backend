import { HttpClient, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { KpiWidgetDto } from "../../main/model/KpiWidgetDto";
import { AppRestService } from "../../main/services/appRest.service";
import { Responsable } from "../../profile/model/Responsable";

@Injectable({
  providedIn: 'root'
})
export class KpiWidgetService extends AppRestService<KpiWidgetDto> {
  constructor(http: HttpClient) {
    super(http, "kpi");
  }

  getKpiWidgetsByPage(displayedPageCode: string, timeScale: string, responsables: Responsable[]) {
    return this.getList(new HttpParams().set("displayedPageCode", displayedPageCode).set("timeScale", timeScale).set("responsableIds", responsables.map(s => s.id).join(",")), "kpi-widgets");
  }

  getKpiWidgetSerieValues(kpi: KpiWidgetDto, responsables: Responsable[], timeScale: string) {
    return this.getList(new HttpParams().set("kpiCrmId", kpi.kpiCrm.id).set("timeScale", timeScale).set("responsableIds", responsables.map(s => s.id).join(",")), "kpi-values");
  }

}
