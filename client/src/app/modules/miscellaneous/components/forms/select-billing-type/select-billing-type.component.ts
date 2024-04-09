import { Component, Input, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { BillingType } from 'src/app/modules/miscellaneous/model/BillingType';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { BillingTypeService } from '../../../services/billing.type.service';
import { GenericSelectComponent } from '../generic-select/generic-select.component';

@Component({
  selector: 'select-billing-type',
  templateUrl: './../generic-select/generic-select.component.html',
  styleUrls: ['./../generic-select/generic-select.component.html']
})
export class SelectBillingTypeComponent extends GenericSelectComponent<BillingType> implements OnInit {

  types: BillingType[] = [] as Array<BillingType>;
  @Input() availableBillingTypes: BillingType[] | undefined;
  @Input() displayOnlyOptionnal: boolean = false;

  constructor(private formBuild: UntypedFormBuilder, private billingTypeService: BillingTypeService, private userNoteService2: UserNoteService) {
    super(formBuild, userNoteService2);
  }

  initTypes(): void {
    this.billingTypeService.getBillingTypes().subscribe(response => {
      let finalTypes = response;
      if (this.availableBillingTypes && this.availableBillingTypes.length > 0) {
        let filteredType = [];
        for (let availableBillingType of this.availableBillingTypes) {
          for (let type of response) {
            if (type.id == availableBillingType.id)
              filteredType.push(type);
          }
        }
        finalTypes = filteredType;
      }
      if (this.displayOnlyOptionnal) {
        let optionnalTypes = [];
        if (finalTypes && finalTypes.length > 0)
          for (let finalType of finalTypes)
            if (finalType.isOptionnal)
              optionnalTypes.push(finalType);
        finalTypes = optionnalTypes;
      }
      this.types = finalTypes;
    });
  }
}
