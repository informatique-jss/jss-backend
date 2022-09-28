import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { AccountingAccount } from 'src/app/modules/accounting/model/AccountingAccount';
import { AccountingAccountService } from 'src/app/modules/accounting/services/accounting.account.service';
import { SortTableColumn } from 'src/app/modules/miscellaneous/model/SortTableColumn';
import { AppService } from 'src/app/services/app.service';
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

  definedMatTableColumn() {
    this.displayedColumns = [];
    this.displayedColumns.push({ id: "id", fieldName: "id", label: "Identifiant technique" } as SortTableColumn);
    this.displayedColumns.push({ id: "code", fieldName: "code", label: "Codification fonctionnelle", valueFonction: (element: any, elements: any[], column: SortTableColumn, columns: SortTableColumn[]) => { if (element && column) return this.getElementCode(element); return "" } } as SortTableColumn);
  }

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
