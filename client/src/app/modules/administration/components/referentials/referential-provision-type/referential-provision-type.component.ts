import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { BillingType } from 'src/app/modules/miscellaneous/model/BillingType';
import { ConstantService } from 'src/app/modules/miscellaneous/services/constant.service';
import { ProvisionType } from 'src/app/modules/quotation/model/ProvisionType';
import { ProvisionTypeService } from 'src/app/modules/quotation/services/provision.type.service';
import { AppService } from 'src/app/services/app.service';
import { GenericReferentialComponent } from '../generic-referential/generic-referential-component';

@Component({
  selector: 'referential-provision-type',
  templateUrl: 'referential-provision-type.component.html',
  styleUrls: ['./../generic-referential/generic-referential.component.css']
})
export class ReferentialProvisionTypeComponent extends GenericReferentialComponent<ProvisionType> implements OnInit {
  constructor(private provisionTypeService: ProvisionTypeService,
    private formBuilder2: FormBuilder,
    private constantService: ConstantService,
    private appService2: AppService,) {
    super(formBuilder2, appService2);
  }

  assignationTypeEmployee = this.constantService.getAssignationTypeEmployee();

  mapEntities() {
    if (this.selectedEntity)
      this.selectedEntity.assignationType = this.assignationTypeEmployee;
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

  deleteBillingType(billingTypeIn: BillingType) {
    if (this.selectedEntity && this.selectedEntity.billingTypes)
      for (let i = 0; i < this.selectedEntity.billingTypes.length; i++)
        if (this.selectedEntity.billingTypes[i].id == billingTypeIn.id)
          this.selectedEntity.billingTypes.splice(i, 1);
  }
}
