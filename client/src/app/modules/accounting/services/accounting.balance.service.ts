import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { toIsoString } from 'src/app/libs/FormatHelper';
import { AppRestService } from 'src/app/services/appRest.service';
import { AccountingBalance } from '../model/AccountingBalance';
import { AccountingBalanceSearch } from '../model/AccountingBalanceSearch';

@Injectable({
  providedIn: 'root'
})
export class AccountingBalanceService extends AppRestService<AccountingBalance>{

  constructor(http: HttpClient) {
    super(http, "accounting");
  }

  searchAccountingBalance(accountingBalanceSearch: AccountingBalanceSearch) {
    return this.postList(new HttpParams(), "accounting-balance/search", accountingBalanceSearch);
  }

  exportBalance(accountingBalanceSearch: AccountingBalanceSearch) {
    this.downloadGet(new HttpParams().set("accountingClassId", accountingBalanceSearch.accountingClass ? accountingBalanceSearch.accountingClass.id : "").set("principalAccountingAccountId", accountingBalanceSearch.principalAccountingAccount ? accountingBalanceSearch.principalAccountingAccount.id + "" : "0").set("accountingAccountId", accountingBalanceSearch.accountingAccount ? accountingBalanceSearch.accountingAccount.id! : "").set("startDate", toIsoString(accountingBalanceSearch.startDate!)).set("endDate", toIsoString(accountingBalanceSearch.endDate!)), "accounting-balance/export");
  }

  searchAccountingBalanceGenerale(accountingBalanceSearch: AccountingBalanceSearch) {
    return this.postList(new HttpParams(), "accounting-balance/generale/search", accountingBalanceSearch);
  }

  exportBalanceGenerale(accountingBalanceSearch: AccountingBalanceSearch) {
    this.downloadGet(new HttpParams().set("accountingClassId", accountingBalanceSearch.accountingClass ? accountingBalanceSearch.accountingClass.id : "").set("principalAccountingAccountId", accountingBalanceSearch.principalAccountingAccount ? accountingBalanceSearch.principalAccountingAccount.id + "" : "0").set("accountingAccountId", accountingBalanceSearch.accountingAccount ? accountingBalanceSearch.accountingAccount.id! : "").set("startDate", toIsoString(accountingBalanceSearch.startDate!)).set("endDate", toIsoString(accountingBalanceSearch.endDate!)), "accounting-balance/generale/export");
  }

}
