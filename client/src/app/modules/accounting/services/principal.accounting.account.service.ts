import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { PrincipalAccountingAccount } from '../../accounting/model/PrincipalAccountingAccount';

@Injectable({
  providedIn: 'root'
})
export class PrincipalAccountingAccountService extends AppRestService<PrincipalAccountingAccount>{

  constructor(http: HttpClient) {
    super(http, "accounting");
  }

  getPrincipalAccountingAccounts() {
    return this.getList(new HttpParams(), "principal-accounting-accounts");
  }
  
   addOrUpdatePrincipalAccountingAccount(principalAccountingAccount: PrincipalAccountingAccount) {
    return this.addOrUpdate(new HttpParams(), "principal-accounting-account", principalAccountingAccount, "Enregistré", "Erreur lors de l'enregistrement");
  }

}
