import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { MatAutocompleteSelectedEvent } from '@angular/material/autocomplete';
import { Observable, map, startWith } from 'rxjs';
import { PrincipalAccountingAccount } from 'src/app/modules/accounting/model/PrincipalAccountingAccount';
import { PrincipalAccountingAccountService } from '../../../../accounting/services/principal.accounting.account.service';
import { GenericChipsComponent } from '../generic-chips/generic-chips.component';

@Component({
  selector: 'chips-principal-accounting-account',
  templateUrl: './chips-principal-accounting-account.component.html',
  styleUrls: ['./chips-principal-accounting-account.component.css']
})
export class ChipsPrincipalAccountingAccountComponent extends GenericChipsComponent<PrincipalAccountingAccount> implements OnInit {

  principalAccountingAccounts: PrincipalAccountingAccount[] = [] as Array<PrincipalAccountingAccount>;
  filteredPrincipalAccountingAccounts: Observable<PrincipalAccountingAccount[]> | undefined;
  @ViewChild('PrincipalAccountingAccountInput') PrincipalAccountingAccountInput: ElementRef<HTMLInputElement> | undefined;

  constructor(private formBuild: UntypedFormBuilder, private principalAccountingAccountService: PrincipalAccountingAccountService,) {
    super(formBuild)
  }

  callOnNgInit(): void {
    this.principalAccountingAccountService.getPrincipalAccountingAccounts().subscribe(response => {
      this.principalAccountingAccounts = response;
    })
    if (this.form)
      this.filteredPrincipalAccountingAccounts = this.form.get(this.propertyName)?.valueChanges.pipe(
        startWith(''),
        map(value => this._filterByName(this.principalAccountingAccounts, value))
      );
  }


  validateInput(value: string): boolean {
    return true;
  }

  setValueToObject(value: string, object: PrincipalAccountingAccount): PrincipalAccountingAccount {
    return object;
  }

  getValueFromObject(object: PrincipalAccountingAccount): string {
    return object.label;
  }

  private _filterByName(inputList: any, value: string): any {
    const filterValue = (value != undefined && value.toLowerCase != undefined) ? value.toLowerCase() : "";
    return inputList.filter((input: any) => input.label != undefined && (input.label.toLowerCase().includes(filterValue) || input.code.toLowerCase().includes(filterValue)));
  }

  addPrincipalAccountingAccount(event: MatAutocompleteSelectedEvent): void {
    if (this.form != undefined) {
      if (!this.model)
        this.model = [] as Array<PrincipalAccountingAccount>;
      // Do not add twice
      if (this.model.map(PrincipalAccountingAccount => PrincipalAccountingAccount.id).indexOf(event.option.value.id) >= 0)
        return;
      if (event.option && event.option.value && event.option.value.id)
        this.model.push(event.option.value);
      this.modelChange.emit(this.model);
      this.form.get(this.propertyName)?.setValue(null);
      this.PrincipalAccountingAccountInput!.nativeElement.value = '';
    }
  }
}
