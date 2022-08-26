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
    let params = new HttpParams();
    params.set("accountingClassId", accountingBalanceSearch.accountingClass ? accountingBalanceSearch.accountingClass.id : "");
    params.set("accountingAccountNumber", accountingBalanceSearch.accountingAccountNumber ? accountingBalanceSearch.accountingAccountNumber : "");
    params.set("accountingAccountId", accountingBalanceSearch.accountingAccount ? accountingBalanceSearch.accountingAccount.id! : "");
    params.set("startDate", toIsoString(accountingBalanceSearch.startDate!));
    params.set("endDate", toIsoString(accountingBalanceSearch.endDate!));
    this.downloadGet(params, "grand-livre/export");
  }
}
