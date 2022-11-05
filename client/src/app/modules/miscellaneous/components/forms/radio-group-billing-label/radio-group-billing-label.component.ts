import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { BillingLabelType } from 'src/app/modules/tiers/model/BillingLabelType';
import { BillingLabelTypeService } from 'src/app/modules/tiers/services/billing.label.type.service';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { GenericRadioGroupComponent } from '../generic-radio-group/generic-radio-group.component';

@Component({
  selector: 'radio-group-billing-label',
  templateUrl: './radio-group-billing-label.component.html',
  styleUrls: ['./radio-group-billing-label.component.css']
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
      if (this.model == null || this.model.id == null) {
        this.model = this.types[0];
        this.modelChange.emit(this.model);
      }
    })
  }
}
