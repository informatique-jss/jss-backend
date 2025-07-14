import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { SHARED_IMPORTS } from '../../../../../libs/SharedImports';
import { QUOTATION_TYPE_ORDER, QUOTATION_TYPE_QUOTATION, QuotationType } from '../../../../quotation/model/QuotationType';
import { GenericRadioGroupComponent } from '../generic-radio-group/generic-radio-group.component';

@Component({
  selector: 'radio-group-quotation-type',
  templateUrl: './radio-group-quotation-type.component.html',
  styleUrls: ['./radio-group-quotation-type.component.css'],
  standalone: true,
  imports: [SHARED_IMPORTS]
})
export class RadioGroupQuotationTypeComponent extends GenericRadioGroupComponent<QuotationType> implements OnInit {
  types: QuotationType[] = [] as Array<QuotationType>;

  constructor(
    private formBuild: UntypedFormBuilder) {
    super(formBuild);
  }

  initTypes(): void {
    this.types.push(QUOTATION_TYPE_QUOTATION, QUOTATION_TYPE_ORDER);
  }
}
