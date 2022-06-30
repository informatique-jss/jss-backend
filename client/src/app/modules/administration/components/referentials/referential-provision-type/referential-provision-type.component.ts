import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { BillingType } from 'src/app/modules/miscellaneous/model/BillingType';
import { ProvisionType } from 'src/app/modules/quotation/model/ProvisionType';
import { ProvisionTypeService } from 'src/app/modules/quotation/services/provision.type.service';
import { GenericReferentialComponent } from '../generic-referential/generic-referential-component';

@Component({
  selector: 'referential-provision-type',
  templateUrl: 'referential-provision-type.component.html',
  styleUrls: ['./../generic-referential/generic-referential.component.css']
})
export class ReferentialProvisionTypeComponent extends GenericReferentialComponent<ProvisionType> implements OnInit {
  constructor(private provisionTypeService: ProvisionTypeService,
    private formBuilder2: FormBuilder) {
    super(formBuilder2);
  }

  getAddOrUpdateObservable(): Observable<ProvisionType> {
    return this.provisionTypeService.addOrUpdateProvisionType(this.selectedEntity!);
  }
  getGetObservable(): Observable<ProvisionType[]> {
    return this.provisionTypeService.getProvisionTypes();
  }

  addBillingType() {
    if (this.selectedEntity)
      if (!this.selectedEntity.billingTypes)
        this.selectedEntity.billingTypes = [] as Array<BillingType>;
    this.selectedEntity?.billingTypes.push({} as BillingType);
  }
}
