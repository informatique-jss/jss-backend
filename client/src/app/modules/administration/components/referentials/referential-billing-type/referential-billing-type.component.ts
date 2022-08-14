import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { BillingType } from 'src/app/modules/miscellaneous/model/BillingType';
import { BillingTypeService } from 'src/app/modules/miscellaneous/services/billing.type.service';
import { GenericReferentialComponent } from '../generic-referential/generic-referential-component';

@Component({
  selector: 'referential-billing-type',
  templateUrl: 'referential-billing-type.component.html',
  styleUrls: ['./../generic-referential/generic-referential.component.css']
})
export class ReferentialBillingTypeComponent extends GenericReferentialComponent<BillingType> implements OnInit {
  constructor(private billingTypeService: BillingTypeService,
    private formBuilder2: FormBuilder) {
    super(formBuilder2);
  }

  mapEntities() {
    for (const entity of this.entities) {
      if (!entity.canOverridePrice)
        entity.canOverridePrice = false;
      if (!entity.isPriceBasedOnCharacterNumber)
        entity.isPriceBasedOnCharacterNumber = false;
    }
  }

  getAddOrUpdateObservable(): Observable<BillingType> {
    return this.billingTypeService.addOrUpdateBillingType(this.selectedEntity!);
  }
  getGetObservable(): Observable<BillingType[]> {
    return this.billingTypeService.getBillingTypes();
  }
}
