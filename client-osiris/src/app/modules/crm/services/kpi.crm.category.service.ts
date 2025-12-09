import { HttpClient, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { AppRestService } from "../../main/services/appRest.service";
import { KpiCrmCategory } from "../model/KpiCrmCategory";

@Injectable({
  providedIn: 'root'
})
export class KpiCrmCategoryService extends AppRestService<KpiCrmCategory> {
  constructor(http: HttpClient) {
    super(http, "kpi");
  }

  getKpiCrmCategories() {
    return this.getList(new HttpParams(), "kpis-crm-categories");
  }
}
