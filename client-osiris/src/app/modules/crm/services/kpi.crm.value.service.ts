import { HttpClient, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { AppRestService } from "../../main/services/appRest.service";
import { KpiCrmJob } from "../model/KpiCrmJob";
import { KpiCrmSearchModel } from "../model/KpiCrmSearchModel";
import { KpiCrmValue } from "../model/KpiCrmValue";
import { KpiCrmValuePayload } from "../model/KpiCrmValuePayload";

@Injectable({
  providedIn: 'root'
})
export class KpiCrmValueService extends AppRestService<KpiCrmValue> {
  constructor(http: HttpClient) {
    super(http, "kpi");
  }

  getJobForAggregateValuesForTiersList(kpiCrmKey: string, searchModel: KpiCrmSearchModel) {
    return this.postItem(new HttpParams().set("kpiCrmKey", kpiCrmKey), "kpi-crm/values/tiers", searchModel) as any as Observable<KpiCrmJob>;
  }

  getKpiCrmValuePayloadAggregatedByTiersAndDate(kpiCrmKey: string, searchModel: KpiCrmSearchModel) {
    return this.postItem(new HttpParams().set("kpiCrmKey", kpiCrmKey), "kpi-crm/values/tiers/details", searchModel) as any as Observable<KpiCrmValuePayload>;
  }

  getJobResultForAggregateValuesForTiersList(jobId: string) {
    return this.get(new HttpParams().set("jobId", jobId), "kpi-crm/values/tiers/result") as any as Observable<KpiCrmJob>;
  }

  getJobForAggregateValuesForResponsableList(kpiCrmKey: string, searchModel: KpiCrmSearchModel) {
    return this.postItem(new HttpParams().set("kpiCrmKey", kpiCrmKey), "kpi-crm/values/responsable", searchModel) as any as Observable<KpiCrmJob>;
  }

  getKpiCrmValuePayloadAggregatedByResponsableAndDate(kpiCrmKey: string, searchModel: KpiCrmSearchModel) {
    return this.postItem(new HttpParams().set("kpiCrmKey", kpiCrmKey), "kpi-crm/values/responsable/details", searchModel) as any as Observable<KpiCrmValuePayload>;
  }

  getJobResultForAggregateValuesForResponsableList(jobId: string) {
    return this.get(new HttpParams().set("jobId", jobId), "kpi-crm/values/responsable/result") as any as Observable<KpiCrmJob>;
  }
}
