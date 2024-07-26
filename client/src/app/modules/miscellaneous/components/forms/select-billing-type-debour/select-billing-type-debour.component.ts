import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { BillingType } from '../../../model/BillingType';
import { BillingTypeService } from '../../../services/billing.type.service';
import { GenericSelectComponent } from '../generic-select/generic-select.component';

@Component({
  selector: 'select-billing-type-debour',
  templateUrl: './../generic-select/generic-select.component.html',
  styleUrls: ['./../generic-select/generic-select.component.css']
})
export class SelectBillingTypeDebourComponent extends GenericSelectComponent<BillingType> implements OnInit {

  types: BillingType[] = [] as Array<BillingType>;

  constructor(private formBuild: UntypedFormBuilder, private billingTypeService: BillingTypeService) {
    super(formBuild)
  }

  initTypes(): void {
    this.billingTypeService.getBillingTypesDebour().subscribe(response => {
      this.types = response;
    })
  }
}
