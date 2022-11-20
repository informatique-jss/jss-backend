import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { BillingClosureType } from 'src/app/modules/tiers/model/BillingClosureType';
import { BillingClosureTypeService } from 'src/app/modules/tiers/services/billing.closure.type.service';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { GenericRadioGroupComponent } from '../generic-radio-group/generic-radio-group.component';

@Component({
  selector: 'radio-group-billing-closure',
  templateUrl: '../generic-radio-group/generic-radio-group-code.component.html',
  styleUrls: ['../generic-radio-group/generic-radio-group.component.css']
})
export class RadioGroupBillingClosureComponent extends GenericRadioGroupComponent<BillingClosureType> implements OnInit {
  types: BillingClosureType[] = [] as Array<BillingClosureType>;

  constructor(
    private formBuild: UntypedFormBuilder, private billingclosuretypeService: BillingClosureTypeService, private userNoteService2: UserNoteService) {
    super(formBuild, userNoteService2);
  }

  initTypes(): void {
    this.billingclosuretypeService.getBillingClosureTypes().subscribe(response => { this.types = response })
  }
}
