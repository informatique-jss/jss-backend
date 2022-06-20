import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { BillingClosureType } from 'src/app/modules/tiers/model/BillingClosureType';
import { BillingClosureTypeService } from 'src/app/modules/tiers/services/billing.closure.type.service';
import { GenericRadioGroupComponent } from '../generic-radio-group/generic-radio-group.component';

@Component({
  selector: 'radio-group-billing-closure',
  templateUrl: './radio-group-billing-closure.component.html',
  styleUrls: ['./radio-group-billing-closure.component.css']
})
export class RadioGroupBillingClosureComponent extends GenericRadioGroupComponent<BillingClosureType> implements OnInit {
  types: BillingClosureType[] = [] as Array<BillingClosureType>;

  constructor(
    private formBuild: FormBuilder, private billingclosuretypeService: BillingClosureTypeService) {
    super(formBuild);
  }

  initTypes(): void {
    this.billingclosuretypeService.getBillingClosureTypes().subscribe(response => { this.types = response })
  }
}
