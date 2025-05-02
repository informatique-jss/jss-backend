import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { BillingLabelType } from '../../../../my-account/model/BillingLabelType';
import { individual, notIndividual } from '../../../../quotation/model/AffaireType';
import { GenericRadioGroupComponent } from '../generic-radio-group/generic-radio-group.component';

@Component({
  selector: 'radio-group-billing-label',
  templateUrl: './radio-group-billing-label.component.html',
  standalone: false
})
export class RadioGroupBillingLabelComponent extends GenericRadioGroupComponent<BillingLabelType> implements OnInit {
  types: BillingLabelType[] = [] as Array<BillingLabelType>;

  constructor(
    private formBuild: UntypedFormBuilder) {
    super(formBuild);
  }

  initTypes(): void {
    this.types.push(notIndividual, individual);
  }
}
