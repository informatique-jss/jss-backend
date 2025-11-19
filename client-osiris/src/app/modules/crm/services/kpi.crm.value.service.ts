import { HttpClient, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { formatDateUs } from "../../../libs/FormatHelper";
import { AppRestService } from "../../main/services/appRest.service";
import { KpiCrmJob } from "../model/KpiCrmJob";
import { KpiCrmValue } from "../model/KpiCrmValue";
import { KpiCrmValuePayload } from "../model/KpiCrmValuePayload";

@Injectable({
  providedIn: 'root'
})
export class KpiCrmValueService extends AppRestService<KpiCrmValue> {
  constructor(http: HttpClient) {
    super(http, "kpi");
  }

  getJobForAggregateValuesForTiersList(kpiCrmKey: string, startDate: Date, endDate: Date, salesEmployeeId: number, tiersIds: number[], isAllTiers: boolean) {
    let params = new HttpParams().set("kpiCrmKey", kpiCrmKey).set("startDate", formatDateUs(startDate)).set("endDate", formatDateUs(endDate)).set("isAllTiers", isAllTiers);
    if (salesEmployeeId) {
      params = params.set("salesEmployeeId", salesEmployeeId);
    }
    return this.postItem(params, "kpi-crm/values/tiers", tiersIds) as any as Observable<KpiCrmJob>;
  }

  getKpiCrmValuePayloadAggregatedByTiersAndDate(kpiCrmKey: string, startDate: Date, endDate: Date, salesEmployeeId: number, tiersIds: number[], isAllTiers: boolean) {
    let params = new HttpParams().set("kpiCrmKey", kpiCrmKey).set("startDate", formatDateUs(startDate)).set("endDate", formatDateUs(endDate)).set("isAllTiers", isAllTiers);
    if (salesEmployeeId) {
      params = params.set("salesEmployeeId", salesEmployeeId);
    }
    return this.postItem(params, "kpi-crm/values/tiers/details", tiersIds) as any as Observable<KpiCrmValuePayload>;
  }

  getJobResultForAggregateValuesForTiersList(jobId: string) {
    return this.get(new HttpParams().set("jobId", jobId), "kpi-crm/values/tiers/result") as any as Observable<KpiCrmJob>;
  }

  getJobForAggregateValuesForResponsableList(kpiCrmKey: string, startDate: Date, endDate: Date, salesEmployeeId: number, responsableIds: number[], isAllResponsable: boolean) {
    let params = new HttpParams().set("kpiCrmKey", kpiCrmKey).set("startDate", formatDateUs(startDate)).set("endDate", formatDateUs(endDate)).set("isAllResponsable", isAllResponsable);
    if (salesEmployeeId) {
      params = params.set("salesEmployeeId", salesEmployeeId);
    }
    return this.postItem(params, "kpi-crm/values/responsable", responsableIds) as any as Observable<KpiCrmJob>;
  }

  getKpiCrmValuePayloadAggregatedByResponsableAndDate(kpiCrmKey: string, startDate: Date, endDate: Date, salesEmployeeId: number, responsableIds: number[], isAllResponsable: boolean) {
    let params = new HttpParams().set("kpiCrmKey", kpiCrmKey).set("startDate", formatDateUs(startDate)).set("endDate", formatDateUs(endDate)).set("isAllResponsable", isAllResponsable);
    if (salesEmployeeId) {
      params = params.set("salesEmployeeId", salesEmployeeId);
    }
    return this.postItem(params, "kpi-crm/values/responsable/details", responsableIds) as any as Observable<KpiCrmValuePayload>;
  }

  getJobResultForAggregateValuesForResponsableList(jobId: string) {
    return this.get(new HttpParams().set("jobId", jobId), "kpi-crm/values/responsable/result") as any as Observable<KpiCrmJob>;
  }
}
