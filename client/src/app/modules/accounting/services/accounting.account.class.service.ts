import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/appRest.service';
import { AccountingAccountClass } from '../../accounting/model/AccountingAccountClass';

@Injectable({
  providedIn: 'root'
})
export class AccountingAccountClassService extends AppRestService<AccountingAccountClass>{

  constructor(http: HttpClient) {
    super(http, "accounting");
  }

  getAccountingAccountClasses() {
    return this.getList(new HttpParams(), "accounting-account-classes");
  }
  
   addOrUpdateAccountingAccountClass(accountingAccountClass: AccountingAccountClass) {
    return this.addOrUpdate(new HttpParams(), "accounting-account-class", accountingAccountClass, "Enregistré", "Erreur lors de l'enregistrement");
  }

}
