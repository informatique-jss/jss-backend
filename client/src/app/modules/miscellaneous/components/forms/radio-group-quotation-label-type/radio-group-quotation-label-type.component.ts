import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { QuotationLabelType } from 'src/app/modules/quotation/model/QuotationLabelType';
import { QuotationLabelTypeService } from 'src/app/modules/quotation/services/quotation.label.type.service';
import { GenericRadioGroupComponent } from '../generic-radio-group/generic-radio-group.component';

@Component({
  selector: 'radio-group-quotation-label-type',
  templateUrl: './radio-group-quotation-label-type.component.html',
  styleUrls: ['./radio-group-quotation-label-type.component.css']
})
export class RadioGroupQuotationLabelTypeComponent extends GenericRadioGroupComponent<QuotationLabelType> implements OnInit {
  types: QuotationLabelType[] = [] as Array<QuotationLabelType>;

  constructor(
    private formBuild: UntypedFormBuilder, private quotationlabeltypeService: QuotationLabelTypeService) {
    super(formBuild);
  }

  initTypes(): void {
    this.quotationlabeltypeService.getQuotationLabelTypes().subscribe(response => { this.types = response })
  }
}
