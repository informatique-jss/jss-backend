import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { AccountingAccountClass } from '../../accounting/model/AccountingAccountClass';

@Injectable({
  providedIn: 'root'
})
export class AccountingAccountClassService extends AppRestService<AccountingAccountClass>{

  constructor(http: HttpClient) {
    super(http, "accounting");
  }

  getAccountingAccountClasses() {
    return this.getListCached(new HttpParams(), "accounting-account-classes");
  }

  addOrUpdateAccountingAccountClass(accountingAccountClass: AccountingAccountClass) {
    this.clearListCache(new HttpParams(), "accounting-account-classes");
    return this.addOrUpdate(new HttpParams(), "accounting-account-class", accountingAccountClass, "Enregistré", "Erreur lors de l'enregistrement");
  }

}
