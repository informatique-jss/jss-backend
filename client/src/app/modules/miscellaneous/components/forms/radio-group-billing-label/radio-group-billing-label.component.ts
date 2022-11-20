import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { BillingLabelType } from 'src/app/modules/tiers/model/BillingLabelType';
import { BillingLabelTypeService } from 'src/app/modules/tiers/services/billing.label.type.service';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { GenericRadioGroupComponent } from '../generic-radio-group/generic-radio-group.component';

@Component({
  selector: 'radio-group-billing-label',
  templateUrl: '../generic-radio-group/generic-radio-group-code.component.html',
  styleUrls: ['../generic-radio-group/generic-radio-group.component.css']
})
export class RadioGroupBillingLabelComponent extends GenericRadioGroupComponent<BillingLabelType> implements OnInit {
  types: BillingLabelType[] = [] as Array<BillingLabelType>;

  constructor(
    private formBuild: UntypedFormBuilder, private billinglabeltypeService: BillingLabelTypeService, private userNoteService2: UserNoteService) {
    super(formBuild, userNoteService2);
  }

  initTypes(): void {
    this.billinglabeltypeService.getBillingLabelTypes().subscribe(response => {
      this.types = response;
    })
  }
}
