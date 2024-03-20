import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { ConstantService } from 'src/app/modules/miscellaneous/services/constant.service';
import { Service } from 'src/app/modules/quotation/model/Service';
import { ServiceService } from 'src/app/modules/quotation/services/service.service';
import { AppService } from 'src/app/services/app.service';
import { GenericReferentialComponent } from '../generic-referential/generic-referential-component';

@Component({
  selector: 'referential-service',
  templateUrl: './../generic-referential/generic-referential.component.html',
  styleUrls: ['./../generic-referential/generic-referential.component.css']
})
export class ReferentialServiceComponent extends GenericReferentialComponent<Service> implements OnInit {
  constructor(private serviceService: ServiceService,
    private formBuilder2: FormBuilder,
    private constantService: ConstantService,
    private appService2: AppService,) {
    super(formBuilder2, appService2);
  }

  getAddOrUpdateObservable(): Observable<Service> {
    return this.serviceService.addOrUpdateService(this.selectedEntity!);
  }
  getGetObservable(): Observable<Service[]> {
    return this.serviceService.getServices();
  }

  addService() {
    //if (this.selectedEntity)
    //  if (!this.selectedEntity.billingTypes)
    //    this.selectedEntity.billingTypes = [] as Array<BillingType>;
    //this.selectedEntity?.billingTypes.push({} as BillingType);
  }

  deleteBillingType(serviceIn: Service) {
    //if (this.selectedEntity && this.selectedEntity.billingTypes)
    //  for (let i = 0; i < this.selectedEntity.billingTypes.length; i++)
    //    if (this.selectedEntity.billingTypes[i].id == billingTypeIn.id)
    //      this.selectedEntity.billingTypes.splice(i, 1);
  }
}
