import { Component, Input, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { AccountingAccount } from 'src/app/modules/accounting/model/AccountingAccount';
import { PrincipalAccountingAccount } from 'src/app/modules/accounting/model/PrincipalAccountingAccount';
import { AccountingAccountService } from 'src/app/modules/accounting/services/accounting.account.service';
import { GenericAutocompleteComponent } from '../generic-autocomplete/generic-autocomplete.component';
import { AppService } from '../../../../../services/app.service';

@Component({
  selector: 'autocomplete-accounting-account',
  templateUrl: '../generic-autocomplete/generic-autocomplete.component.html',
  styleUrls: ['../generic-autocomplete/generic-autocomplete.component.css']
})
export class AutocompleteAccountingAccountComponent extends GenericAutocompleteComponent<AccountingAccount, AccountingAccount> implements OnInit {

  /**
   * If defined, only account with specified subnumber are searchable
   */
  @Input() filteredAccountSubNumber: number | undefined;

  /**
 * If defined, only account with specified principal are searchable
 */
  @Input() filteredAccountPrincipal: PrincipalAccountingAccount | undefined;

  constructor(private formBuild: UntypedFormBuilder, private accountingAccountService: AccountingAccountService, private appService3: AppService) {
    super(formBuild, appService3)
  }

  searchEntities(value: string): Observable<AccountingAccount[]> {
    return this.accountingAccountService.getAccountingAccountByLabel(value);
  }


  displayLabel(object: any): string {
    return object ? object.label + " - " + object.principalAccountingAccount.code + object.accountingAccountSubNumber.toString().padStart(8 - object.principalAccountingAccount.code.length, '0') : '';
  }

  mapResponse(response: AccountingAccount[]): AccountingAccount[] {
    if (response && this.filteredAccountSubNumber) {
      let outAccountingAccount = [];
      for (let account of response)
        if (account.accountingAccountSubNumber == this.filteredAccountSubNumber)
          outAccountingAccount.push(account);
      return outAccountingAccount;
    } else if (response && this.filteredAccountPrincipal) {
      let outAccountingAccount = [];
      for (let account of response)
        if (account.principalAccountingAccount.id == this.filteredAccountPrincipal.id)
          outAccountingAccount.push(account);
      return outAccountingAccount;
    } else
      return response;
  }

  ngOnInit(): void {
    super.ngOnInit();
    if (this.filteredAccountSubNumber)
      this.accountingAccountService.getAccountingAccountByLabel("-" + this.filteredAccountSubNumber).subscribe(response => {
        this.filteredTypes = this.mapResponse(response);
      })
  }


  override getPreviewActionLinkFunction(entity: AccountingAccount): string[] | undefined {
    return ['accounting/view/', entity.id + ""];
  }
}
