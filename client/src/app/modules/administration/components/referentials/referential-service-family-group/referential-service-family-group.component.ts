import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { AppService } from 'src/app/services/app.service';
import { ServiceFamilyGroup } from 'src/app/modules/quotation/model/ServiceFamilyGroup';
import { ServiceFamilyGroupService } from 'src/app/modules/quotation/services/service.family.group.service';
import { GenericReferentialComponent } from '../generic-referential/generic-referential-component';

@Component({
  selector: 'referential-service-family-group',
  templateUrl: './../generic-referential/generic-referential.component.html',
  styleUrls: ['./../generic-referential/generic-referential.component.css']
})
export class ReferentialServiceFamilyGroupComponent extends GenericReferentialComponent<ServiceFamilyGroup> implements OnInit {
  constructor(private serviceFamilyGroupService: ServiceFamilyGroupService,
    private formBuilder2: FormBuilder,
    private appService2: AppService,) {
    super(formBuilder2, appService2);
  }

  getAddOrUpdateObservable(): Observable<ServiceFamilyGroup> {
    return this.serviceFamilyGroupService.addOrUpdateServiceFamilyGroup(this.selectedEntity!);
  }
  getGetObservable(): Observable<ServiceFamilyGroup[]> {
    return this.serviceFamilyGroupService.getServiceFamilyGroups();
  }
}
