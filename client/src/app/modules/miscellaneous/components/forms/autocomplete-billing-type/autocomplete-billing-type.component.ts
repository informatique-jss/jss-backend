import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { BillingType } from '../../../model/BillingType';
import { BillingTypeService } from '../../../services/billing.type.service';
import { GenericLocalAutocompleteComponent } from '../generic-local-autocomplete/generic-local-autocomplete.component';

@Component({
  selector: 'autocomplete-billing-type',
  templateUrl: '../generic-local-autocomplete/generic-local-autocomplete.component.html',
  styleUrls: ['../generic-local-autocomplete/generic-local-autocomplete.component.css'],
})
export class AutocompleteBillingTypeComponent extends GenericLocalAutocompleteComponent<BillingType> implements OnInit {

  types: BillingType[] = [] as Array<BillingType>;

  constructor(private formBuild: UntypedFormBuilder, private billingTypeService: BillingTypeService, private userNoteService2: UserNoteService,) {
    super(formBuild, userNoteService2)
  }

  filterEntities(types: BillingType[], value: string): BillingType[] {
    const filterValue = (value != undefined && value != null && value.toLowerCase != undefined) ? value.toLowerCase() : "";
    return types.filter(billingType =>
      billingType && billingType.label
      && (billingType.label.toLowerCase().includes(filterValue) || billingType.code.includes(filterValue)));
  }

  initTypes(): void {
    this.billingTypeService.getBillingTypes().subscribe(response => this.types = response);
  }

  displayLabel(object: BillingType): string {
    return (object && object.label) ? object.label + " (" + object.code + ")" : '';
  }

}

