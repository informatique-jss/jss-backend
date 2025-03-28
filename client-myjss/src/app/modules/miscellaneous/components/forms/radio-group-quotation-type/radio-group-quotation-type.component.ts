import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { order, quotation, QuotationType } from '../../../../quotation/model/QuotationType';
import { GenericRadioGroupComponent } from '../generic-radio-group/generic-radio-group.component';

@Component({
  selector: 'radio-group-quotation-type',
  templateUrl: './radio-group-quotation-type.component.html',
  styleUrls: ['./radio-group-quotation-type.component.css']
})
export class RadioGroupQuotationTypeComponent extends GenericRadioGroupComponent<QuotationType> implements OnInit {
  types: QuotationType[] = [] as Array<QuotationType>;

  constructor(
    private formBuild: UntypedFormBuilder) {
    super(formBuild);
  }

  initTypes(): void {
    this.types.push(quotation, order);
  }
}
