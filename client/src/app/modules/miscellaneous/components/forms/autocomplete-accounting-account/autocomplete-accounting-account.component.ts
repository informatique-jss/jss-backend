import { Component, Input, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { AccountingAccount } from 'src/app/modules/accounting/model/AccountingAccount';
import { PrincipalAccountingAccount } from 'src/app/modules/accounting/model/PrincipalAccountingAccount';
import { AccountingAccountService } from 'src/app/modules/accounting/services/accounting.account.service';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { GenericAutocompleteComponent } from '../generic-autocomplete/generic-autocomplete.component';

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

  constructor(private formBuild: UntypedFormBuilder, private accountingAccountService: AccountingAccountService, private userNoteService2: UserNoteService,) {
    super(formBuild, userNoteService2)
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
}
