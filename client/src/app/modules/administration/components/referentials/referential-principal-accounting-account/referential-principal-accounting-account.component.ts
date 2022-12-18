import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { PrincipalAccountingAccount } from 'src/app/modules/accounting/model/PrincipalAccountingAccount';
import { PrincipalAccountingAccountService } from 'src/app/modules/accounting/services/principal.accounting.account.service';
import { AppService } from 'src/app/services/app.service';
import { GenericReferentialComponent } from '../generic-referential/generic-referential-component';

@Component({
  selector: 'referential-principal-accounting-account',
  templateUrl: './referential-principal-accounting-account.html',
  styleUrls: ['./../generic-referential/generic-referential.component.css']
})
export class ReferentialPrincipalAccountingAccountComponent extends GenericReferentialComponent<PrincipalAccountingAccount> implements OnInit {
  constructor(private principalAccountingAccountService: PrincipalAccountingAccountService,
    private formBuilder2: FormBuilder,
    private appService2: AppService,) {
    super(formBuilder2, appService2);
  }

  getAddOrUpdateObservable(): Observable<PrincipalAccountingAccount> {
    return this.principalAccountingAccountService.addOrUpdatePrincipalAccountingAccount(this.selectedEntity!);
  }
  getGetObservable(): Observable<PrincipalAccountingAccount[]> {
    return this.principalAccountingAccountService.getPrincipalAccountingAccounts();
  }
}
