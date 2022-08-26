import { HttpClient, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { toIsoString } from "src/app/libs/FormatHelper";
import { AppRestService } from "src/app/services/appRest.service";
import { AccountingBalanceSearch } from "../model/AccountingBalanceSearch";
import { AccountingBalanceViewTitle } from "../model/AccountingBalanceViewTitle";

@Injectable({
  providedIn: 'root'
})
export class AccountingBalanceViewTitleService extends AppRestService<AccountingBalanceViewTitle>{

  constructor(http: HttpClient) {
    super(http, "accounting");
  }

  getBilan(accountingBalanceSearch: AccountingBalanceSearch) {
    return this.getList(new HttpParams().set("startDate", toIsoString(accountingBalanceSearch.startDate!)).set("endDate", toIsoString(accountingBalanceSearch.endDate!)), "bilan");
  }

  getProfitLost(accountingBalanceSearch: AccountingBalanceSearch) {
    return this.getList(new HttpParams().set("startDate", toIsoString(accountingBalanceSearch.startDate!)).set("endDate", toIsoString(accountingBalanceSearch.endDate!)), "profit-lost");
  }

  exportProfitAndLost(startDate: Date, endDate: Date) {
    this.downloadGet(new HttpParams().set("startDate", toIsoString(startDate)).set("endDate", toIsoString(endDate)), "profit-lost/export");
  }

  exportBilan(startDate: Date, endDate: Date) {
    this.downloadGet(new HttpParams().set("startDate", toIsoString(startDate)).set("endDate", toIsoString(endDate)), "bilan/export");
  }
}
