import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { AppService } from 'src/app/app.service';
import { AccountingAccount } from 'src/app/modules/accounting/model/AccountingAccount';
import { AccountingAccountService } from 'src/app/modules/accounting/services/accounting.account.service';
import { GenericReferentialComponent } from '../generic-referential/generic-referential-component';

@Component({
  selector: 'referential-accounting-account',
  templateUrl: 'referential-accounting-account.component.html',
  styleUrls: ['./../generic-referential/generic-referential.component.css']
})
export class ReferentialAccountingAccountComponent extends GenericReferentialComponent<AccountingAccount> implements OnInit {
  constructor(private accountingAccountService: AccountingAccountService,
    private appService2: AppService,
    private formBuilder2: FormBuilder) {
    super(formBuilder2, appService2);
  }

  displayedColumns: string[] = ['id', 'label'];

  getAddOrUpdateObservable(): Observable<AccountingAccount> {
    return this.accountingAccountService.addOrUpdateAccountingAccount(this.selectedEntity!);
  }
  getGetObservable(): Observable<AccountingAccount[]> {
    return this.accountingAccountService.getAccountingAccounts();
  }

  getElementCode(element: AccountingAccount) {
    return element.label;
  }
}
