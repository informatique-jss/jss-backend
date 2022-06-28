import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
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

  constructor(private changeDetectorRef: ChangeDetectorRef,
    private formBuild: UntypedFormBuilder, private billingTypeService: BillingTypeService) {
    super(changeDetectorRef, formBuild);
  }

  initTypes(): void {
    this.billingTypeService.getBillingTypes().subscribe(response => {
      this.types = response;
    })
  }
}
