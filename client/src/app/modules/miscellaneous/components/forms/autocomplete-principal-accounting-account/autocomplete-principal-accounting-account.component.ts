import { Component, Input, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { PrincipalAccountingAccountService } from 'src/app/modules/accounting/services/principal.accounting.account.service';
import { PrincipalAccountingAccount } from '../../../../accounting/model/PrincipalAccountingAccount';
import { GenericLocalAutocompleteComponent } from '../generic-local-autocomplete/generic-local-autocomplete.component';

@Component({
  selector: 'autocomplete-principal-accounting-account',
  templateUrl: '../generic-local-autocomplete/generic-local-autocomplete.component.html',
  styleUrls: ['../generic-local-autocomplete/generic-local-autocomplete.component.css'],
})
export class AutocompletePrincipalAccountingAccountComponent extends GenericLocalAutocompleteComponent<PrincipalAccountingAccount> implements OnInit {

  types: PrincipalAccountingAccount[] = [] as Array<PrincipalAccountingAccount>;
  @Input() label: string = "Compte compatable principal";

  constructor(private formBuild: UntypedFormBuilder,
    private principalAccountingAccountService: PrincipalAccountingAccountService,) {
    super(formBuild)
  }

  filterEntities(types: PrincipalAccountingAccount[], value: string): PrincipalAccountingAccount[] {
    const filterValue = (value != undefined && value != null && value.toLowerCase != undefined) ? value.toLowerCase() : "";
    return types.filter(principalAccountingAccount =>
      principalAccountingAccount && principalAccountingAccount.label
      && (principalAccountingAccount.label.toLowerCase().includes(filterValue) || principalAccountingAccount.code.includes(filterValue)));
  }

  initTypes(): void {
    this.principalAccountingAccountService.getPrincipalAccountingAccounts().subscribe(response => this.types = response);
  }

  displayLabel(object: PrincipalAccountingAccount): string {
    return (object && object.label) ? object.label + " (" + object.code + ")" : '';
  }

}
