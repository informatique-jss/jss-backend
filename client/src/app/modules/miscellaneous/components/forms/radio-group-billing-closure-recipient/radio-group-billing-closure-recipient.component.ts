import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { BillingClosureRecipientType } from 'src/app/modules/tiers/model/BillingClosureRecipientType';
import { BillingClosureRecipientTypeService } from 'src/app/modules/tiers/services/billing.closure.recipient.type.service';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { GenericRadioGroupComponent } from '../generic-radio-group/generic-radio-group.component';

@Component({
  selector: 'radio-group-billing-closure-recipient',
  templateUrl: './radio-group-billing-closure-recipient.component.html',
  styleUrls: ['./radio-group-billing-closure-recipient.component.css']
})
export class RadioGroupBillingClosureRecipientComponent extends GenericRadioGroupComponent<BillingClosureRecipientType> implements OnInit {
  types: BillingClosureRecipientType[] = [] as Array<BillingClosureRecipientType>;

  constructor(
    private formBuild: UntypedFormBuilder, private billingclosurerecipienttypeService: BillingClosureRecipientTypeService, private userNoteService2: UserNoteService) {
    super(formBuild, userNoteService2);
  }

  initTypes(): void {
    this.billingclosurerecipienttypeService.getBillingClosureRecipientTypes().subscribe(response => { this.types = response })
  }
}
