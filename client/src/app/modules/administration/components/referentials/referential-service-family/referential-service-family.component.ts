import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { ServiceFamily } from 'src/app/modules/quotation/model/ServiceFamily';
import { ServiceFamilyService } from 'src/app/modules/quotation/services/service.family.service';
import { AppService } from 'src/app/services/app.service';
import { GenericReferentialComponent } from '../generic-referential/generic-referential-component';

@Component({
  selector: 'referential-service-family',
  templateUrl: './referential-service-family.component.html',
  styleUrls: ['./../generic-referential/generic-referential.component.css']
})
export class ReferentialServiceFamilyComponent extends GenericReferentialComponent<ServiceFamily> implements OnInit {
  constructor(private serviceFamilyService: ServiceFamilyService,
    private formBuilder2: FormBuilder,
    private appService2: AppService,) {
    super(formBuilder2, appService2);
  }

  getAddOrUpdateObservable(): Observable<ServiceFamily> {
    return this.serviceFamilyService.addOrUpdateServiceFamily(this.selectedEntity!);
  }
  getGetObservable(): Observable<ServiceFamily[]> {
    return this.serviceFamilyService.getServiceFamilies();
  }
}
