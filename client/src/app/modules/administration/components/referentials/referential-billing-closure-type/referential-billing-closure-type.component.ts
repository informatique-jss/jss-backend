import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { ActType } from 'src/app/modules/quotation/model/ActType';
import { BillingClosureType } from 'src/app/modules/tiers/model/BillingClosureType';
import { BillingClosureTypeService } from 'src/app/modules/tiers/services/billing.closure.type.service';
import { GenericReferentialComponent } from '../generic-referential/generic-referential-component';

@Component({
  selector: 'referential-billing-closure-type',
  templateUrl: './../generic-referential/generic-referential.component.html',
  styleUrls: ['./../generic-referential/generic-referential.component.css']
})
export class ReferentialBillingClosureTypeComponent extends GenericReferentialComponent<BillingClosureType> implements OnInit {
  constructor(private billingClosureTypeService: BillingClosureTypeService,
    private formBuilder2: FormBuilder) {
    super(formBuilder2);
  }

  getAddOrUpdateObservable(): Observable<ActType> {
    return this.billingClosureTypeService.addOrUpdateBillingClosureType(this.selectedEntity!);
  }
  getGetObservable(): Observable<ActType[]> {
    return this.billingClosureTypeService.getBillingClosureTypes();
  }
}
