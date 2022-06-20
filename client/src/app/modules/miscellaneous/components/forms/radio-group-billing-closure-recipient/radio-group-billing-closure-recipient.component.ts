import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { BillingClosureRecipientType } from 'src/app/modules/tiers/model/BillingClosureRecipientType';
import { BillingClosureRecipientTypeService } from 'src/app/modules/tiers/services/billing.closure.recipient.type.service';
import { GenericRadioGroupComponent } from '../generic-radio-group/generic-radio-group.component';

@Component({
  selector: 'radio-group-billing-closure-recipient',
  templateUrl: './radio-group-billing-closure-recipient.component.html',
  styleUrls: ['./radio-group-billing-closure-recipient.component.css']
})
export class RadioGroupBillingClosureRecipientComponent extends GenericRadioGroupComponent<BillingClosureRecipientType> implements OnInit {
  types: BillingClosureRecipientType[] = [] as Array<BillingClosureRecipientType>;

  constructor(
    private formBuild: FormBuilder, private billingclosurerecipienttypeService: BillingClosureRecipientTypeService) {
    super(formBuild);
  }

  initTypes(): void {
    this.billingclosurerecipienttypeService.getBillingClosureRecipientTypes().subscribe(response => { this.types = response })
  }
}
