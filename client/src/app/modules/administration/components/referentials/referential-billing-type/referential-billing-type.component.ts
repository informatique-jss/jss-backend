import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { BillingType } from 'src/app/modules/miscellaneous/model/BillingType';
import { BillingTypeService } from 'src/app/modules/miscellaneous/services/billing.type.service';
import { GenericReferentialComponent } from '../generic-referential/generic-referential-component';

@Component({
  selector: 'referential-billing-type',
  templateUrl: './../generic-referential/generic-referential.component.html',
  styleUrls: ['./../generic-referential/generic-referential.component.css']
})
export class ReferentialBillingTypeComponent extends GenericReferentialComponent<BillingType> implements OnInit {
  constructor(private billingTypeService: BillingTypeService,
    private formBuilder2: FormBuilder) {
    super(formBuilder2);
  }

  getAddOrUpdateObservable(): Observable<BillingType> {
    return this.billingTypeService.addOrUpdateBillingType(this.selectedEntity!);
  }
  getGetObservable(): Observable<BillingType[]> {
    return this.billingTypeService.getBillingTypes();
  }
}
