import { Component, OnInit, SimpleChanges } from '@angular/core';
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

  ngOnChanges(changes: SimpleChanges) {
    if (changes && this.selectedEntity && !this.selectedEntity.isViewRestricted)
      this.selectedEntity.isViewRestricted = false;
  }

  definedMatTableColumn() {
    this.displayedColumns = [];
    this.displayedColumns.push({ id: "id", fieldName: "id", label: "Identifiant technique" } as SortTableColumn<AccountingAccount>);
    this.displayedColumns.push({ id: "code", fieldName: "code", label: "Codification fonctionnelle", valueFonction: (element: AccountingAccount, column: SortTableColumn<AccountingAccount>) => { if (element && column) return this.getElementCode(element); return "" } } as SortTableColumn<AccountingAccount>);
  }

  getAddOrUpdateObservable(): Observable<AccountingAccount> {
    return this.accountingAccountService.addOrUpdateAccountingAccount(this.selectedEntity!);
  }
  getGetObservable(): Observable<AccountingAccount[]> {
    return this.accountingAccountService.getAccountingAccountByLabel("dummy");
  }

  getElementCode(element: AccountingAccount) {
    return element.label;
  }

  applyFilter(filterValue: any) {
    let filterValueCast = (filterValue as HTMLInputElement);
    filterValue = filterValueCast.value.trim();
    this.searchText = filterValue.toLowerCase();

    if (this.searchText && this.searchText.length > 2)
      this.accountingAccountService.getAccountingAccountByLabel(this.searchText).subscribe(response => {
        this.entities = response;
        this.mapEntities();
        this.definedMatTableColumn();
      })
  }
}
