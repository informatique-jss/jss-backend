import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { BillingCenter } from '../../../model/BillingCenter';
import { BillingCenterService } from '../../../services/billing.center.service';
import { GenericLocalAutocompleteComponent } from '../generic-local-autocomplete/generic-local-autocomplete.component';

@Component({
  selector: 'autocomplete-billing-center',
  templateUrl: './autocomplete-billing-center.component.html',
  styleUrls: ['./autocomplete-billing-center.component.css']
})
export class AutocompleteBillingCenterComponent extends GenericLocalAutocompleteComponent<BillingCenter> implements OnInit {

  types: BillingCenter[] = [] as Array<BillingCenter>;

  constructor(private formBuild: UntypedFormBuilder, private billingCenterService: BillingCenterService) {
    super(formBuild)
  }

  filterEntities(types: BillingCenter[], value: string): BillingCenter[] {
    const filterValue = (value != undefined && value != null && value.toLowerCase != undefined) ? value.toLowerCase() : "";
    return types.filter(billingCenter => billingCenter && billingCenter.label.toLowerCase().includes(filterValue));
  }

  initTypes(): void {
    this.billingCenterService.getBillingCenters().subscribe(response => this.types = response);
  }



}
