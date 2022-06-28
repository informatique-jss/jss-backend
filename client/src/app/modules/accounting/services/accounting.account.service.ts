import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/appRest.service';
import { AccountingAccount } from '../../accounting/model/AccountingAccount';

@Injectable({
  providedIn: 'root'
})
export class AccountingAccountService extends AppRestService<AccountingAccount>{

  constructor(http: HttpClient) {
    super(http, "accounting");
  }

  getAccountingAccounts() {
    return this.getList(new HttpParams(), "accounting-accounts");
  }

  addOrUpdateAccountingAccount(accountingAccount: AccountingAccount) {
    return this.addOrUpdate(new HttpParams(), "accounting-account", accountingAccount, "Enregistré", "Erreur lors de l'enregistrement");
  }

  getAccountingAccountByLabel(label: string) {
    return this.getList(new HttpParams().set("label", label), "accounting-account/search");
  }

}
