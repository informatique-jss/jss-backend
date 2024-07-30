import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { BillingClosureRecipientType } from 'src/app/modules/tiers/model/BillingClosureRecipientType';
import { BillingClosureRecipientTypeService } from 'src/app/modules/tiers/services/billing.closure.recipient.type.service';
import { GenericRadioGroupComponent } from '../generic-radio-group/generic-radio-group.component';
import { AppService } from 'src/app/services/app.service';

@Component({
  selector: 'radio-group-billing-closure-recipient',
  templateUrl: '../generic-radio-group/generic-radio-group-code.component.html',
  styleUrls: ['../generic-radio-group/generic-radio-group.component.css']
})
export class RadioGroupBillingClosureRecipientComponent extends GenericRadioGroupComponent<BillingClosureRecipientType> implements OnInit {
  types: BillingClosureRecipientType[] = [] as Array<BillingClosureRecipientType>;

  constructor(
    private formBuild: UntypedFormBuilder, private billingclosurerecipienttypeService: BillingClosureRecipientTypeService, private appService3: AppService) {
    super(formBuild, appService3);
  }

  initTypes(): void {
    this.billingclosurerecipienttypeService.getBillingClosureRecipientTypes().subscribe(response => { this.types = response })
  }
}
