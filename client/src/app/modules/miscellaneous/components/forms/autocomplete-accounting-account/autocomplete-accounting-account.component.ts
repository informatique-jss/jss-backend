import { ChangeDetectorRef, Component, Input, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { AccountingAccount } from 'src/app/modules/accounting/model/AccountingAccount';
import { AccountingAccountService } from 'src/app/modules/accounting/services/accounting.account.service';
import { Country } from '../../../model/Country';
import { GenericAutocompleteComponent } from '../generic-autocomplete/generic-autocomplete.component';

@Component({
  selector: 'autocomplete-accounting-account',
  templateUrl: './autocomplete-accounting-account.component.html',
  styleUrls: ['./autocomplete-accounting-account.component.css']
})
export class AutocompleteAccountingAccountComponent extends GenericAutocompleteComponent<AccountingAccount, AccountingAccount> implements OnInit {

  /**
   * The model of country property.
   * If undefined, cities are searched worldwide
   */
  @Input() modelCountry: Country | undefined;


  constructor(private formBuild: UntypedFormBuilder, private accountingAccountService: AccountingAccountService, private changeDetectorRef: ChangeDetectorRef) {
    super(formBuild, changeDetectorRef)
  }

  searchEntities(value: string): Observable<AccountingAccount[]> {
    return this.accountingAccountService.getAccountingAccountByLabel(value);
  }
}
