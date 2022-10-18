import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { AccountingAccount } from 'src/app/modules/accounting/model/AccountingAccount';
import { AccountingAccountService } from 'src/app/modules/accounting/services/accounting.account.service';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { GenericAutocompleteComponent } from '../generic-autocomplete/generic-autocomplete.component';

@Component({
  selector: 'autocomplete-accounting-account',
  templateUrl: '../generic-autocomplete/generic-autocomplete.component.html',
  styleUrls: ['../generic-autocomplete/generic-autocomplete.component.css']
})
export class AutocompleteAccountingAccountComponent extends GenericAutocompleteComponent<AccountingAccount, AccountingAccount> implements OnInit {

  constructor(private formBuild: UntypedFormBuilder, private accountingAccountService: AccountingAccountService, private userNoteService2: UserNoteService,) {
    super(formBuild, userNoteService2)
  }

  searchEntities(value: string): Observable<AccountingAccount[]> {
    return this.accountingAccountService.getAccountingAccountByLabel(value);
  }


  displayLabel(object: any): string {
    return object ? object.label + " - " + object.accountingAccountNumber + "-" + object.accountingAccountSubNumber : '';
  }
}
