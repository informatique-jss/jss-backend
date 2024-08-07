import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { BillingClosureType } from 'src/app/modules/tiers/model/BillingClosureType';
import { BillingClosureTypeService } from 'src/app/modules/tiers/services/billing.closure.type.service';
import { GenericRadioGroupComponent } from '../generic-radio-group/generic-radio-group.component';
import { AppService } from 'src/app/services/app.service';

@Component({
  selector: 'radio-group-billing-closure',
  templateUrl: '../generic-radio-group/generic-radio-group-code.component.html',
  styleUrls: ['../generic-radio-group/generic-radio-group.component.css']
})
export class RadioGroupBillingClosureComponent extends GenericRadioGroupComponent<BillingClosureType> implements OnInit {
  types: BillingClosureType[] = [] as Array<BillingClosureType>;

  constructor(
    private formBuild: UntypedFormBuilder, private billingclosuretypeService: BillingClosureTypeService, private appService3: AppService) {
    super(formBuild, appService3);
  }

  initTypes(): void {
    this.billingclosuretypeService.getBillingClosureTypes().subscribe(response => { this.types = response })
  }
}
