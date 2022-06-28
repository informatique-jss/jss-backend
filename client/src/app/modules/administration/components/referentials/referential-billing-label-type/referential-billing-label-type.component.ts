import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { BillingLabelType } from 'src/app/modules/tiers/model/BillingLabelType';
import { BillingLabelTypeService } from 'src/app/modules/tiers/services/billing.label.type.service';
import { GenericReferentialComponent } from '../generic-referential/generic-referential-component';

@Component({
  selector: 'referential-billing-label-type',
  templateUrl: './../generic-referential/generic-referential.component.html',
  styleUrls: ['./../generic-referential/generic-referential.component.css']
})
export class ReferentialBillingLabelTypeComponent extends GenericReferentialComponent<BillingLabelType> implements OnInit {
  constructor(private billingLabelTypeService: BillingLabelTypeService,
    private formBuilder2: FormBuilder) {
    super(formBuilder2);
  }

  getAddOrUpdateObservable(): Observable<BillingLabelType> {
    return this.billingLabelTypeService.addOrUpdateBillingLabelType(this.selectedEntity!);
  }
  getGetObservable(): Observable<BillingLabelType[]> {
    return this.billingLabelTypeService.getBillingLabelTypes();
  }
}
