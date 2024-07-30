import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { BillingLabelType } from 'src/app/modules/tiers/model/BillingLabelType';
import { BillingLabelTypeService } from 'src/app/modules/tiers/services/billing.label.type.service';
import { GenericRadioGroupComponent } from '../generic-radio-group/generic-radio-group.component';
import { AppService } from 'src/app/services/app.service';

@Component({
  selector: 'radio-group-billing-label',
  templateUrl: '../generic-radio-group/generic-radio-group-code.component.html',
  styleUrls: ['../generic-radio-group/generic-radio-group.component.css']
})
export class RadioGroupBillingLabelComponent extends GenericRadioGroupComponent<BillingLabelType> implements OnInit {
  types: BillingLabelType[] = [] as Array<BillingLabelType>;

  constructor(
    private formBuild: UntypedFormBuilder, private billinglabeltypeService: BillingLabelTypeService, private appService3: AppService) {
    super(formBuild, appService3);
  }

  initTypes(): void {
    this.billinglabeltypeService.getBillingLabelTypes().subscribe(response => {
      this.types = response;
    })
  }
}
