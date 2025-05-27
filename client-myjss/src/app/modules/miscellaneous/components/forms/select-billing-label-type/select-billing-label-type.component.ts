import { Component, Input, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { SHARED_IMPORTS } from '../../../../../libs/SharedImports';
import { BillingLabelType } from '../../../../my-account/model/BillingLabelType';
import { BillingLabelTypeService } from '../../../../quotation/services/billing.label.type.service';
import { GenericSelectComponent } from '../generic-select/generic-select.component';

@Component({
  selector: 'select-billing-label-type',
  templateUrl: '../generic-select/generic-select.component.html',
  styleUrls: ['../generic-select/generic-select.component.css'],
  standalone: true,
  imports: [SHARED_IMPORTS]
})
export class SelectBillingLabelTypeComponent extends GenericSelectComponent<BillingLabelType> implements OnInit {

  @Input() types: BillingLabelType[] = [] as Array<BillingLabelType>;

  constructor(private formBuild: UntypedFormBuilder,
    private billingLabelTypeService: BillingLabelTypeService) {
    super(formBuild)
  }

  initTypes(): void {
    this.billingLabelTypeService.getBillingLabelTypes().subscribe(response => {
      this.types = response;
    })
  }
}
