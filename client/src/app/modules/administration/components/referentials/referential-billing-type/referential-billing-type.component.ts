import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { BillingType } from 'src/app/modules/miscellaneous/model/BillingType';
import { BillingTypeService } from 'src/app/modules/miscellaneous/services/billing.type.service';
import { AppService } from 'src/app/services/app.service';
import { GenericReferentialComponent } from '../generic-referential/generic-referential-component';

@Component({
  selector: 'referential-billing-type',
  templateUrl: 'referential-billing-type.component.html',
  styleUrls: ['./../generic-referential/generic-referential.component.css']
})
export class ReferentialBillingTypeComponent extends GenericReferentialComponent<BillingType> implements OnInit {
  constructor(private billingTypeService: BillingTypeService,
    private formBuilder2: FormBuilder,
    private appService2: AppService,) {
    super(formBuilder2, appService2);
  }

  mapEntities() {
    for (const entity of this.entities) {
      if (!entity.canOverridePrice)
        entity.canOverridePrice = false;
      if (!entity.isPriceBasedOnCharacterNumber)
        entity.isPriceBasedOnCharacterNumber = false;
      if (!entity.isOptionnal)
        entity.isOptionnal = false;
      if (!entity.isOverrideVat)
        entity.isOverrideVat = false;
      if (!entity.isNonTaxable)
        entity.isNonTaxable = false;
      if (!entity.isDebour)
        entity.isDebour = false;
      if (!entity.isFee)
        entity.isFee = false;
    }
  }

  getAddOrUpdateObservable(): Observable<BillingType> {
    return this.billingTypeService.addOrUpdateBillingType(this.selectedEntity!);
  }
  getGetObservable(): Observable<BillingType[]> {
    return this.billingTypeService.getBillingTypes();
  }

  toggleFees() {
    if (this.selectedEntity && this.selectedEntity.isFee)
      this.selectedEntity.isDebour = false;
  }

  toggleDebour() {
    if (this.selectedEntity && this.selectedEntity.isDebour)
      this.selectedEntity.isFee = false;
  }
}
