import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { BillingType } from '../../../model/BillingType';
import { BillingTypeService } from '../../../services/billing.type.service';
import { GenericSelectComponent } from '../generic-select/generic-select.component';

@Component({
  selector: 'select-billing-type',
  templateUrl: './select-billing-type.component.html',
  styleUrls: ['./select-billing-type.component.css']
})
export class SelectBillingTypeComponent extends GenericSelectComponent<BillingType> implements OnInit {

  types: BillingType[] = [] as Array<BillingType>;

  constructor(private formBuild: UntypedFormBuilder, private billingTypeService: BillingTypeService, private userNoteService2: UserNoteService,) {
    super(formBuild, userNoteService2)
  }

  initTypes(): void {
    this.billingTypeService.getBillingTypes().subscribe(response => {
      this.types = response;
    })
  }
}
