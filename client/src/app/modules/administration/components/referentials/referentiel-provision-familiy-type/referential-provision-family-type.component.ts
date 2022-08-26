import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { ProvisionFamilyType } from 'src/app/modules/quotation/model/ProvisionFamilyType';
import { ProvisionFamilyTypeService } from 'src/app/modules/quotation/services/provision.family.type.service';
import { AppService } from 'src/app/services/app.service';
import { GenericReferentialComponent } from '../generic-referential/generic-referential-component';

@Component({
  selector: 'referential-provision-family-type',
  templateUrl: './../generic-referential/generic-referential.component.html',
  styleUrls: ['./../generic-referential/generic-referential.component.css']
})
export class ReferentialProvisionFamilyTypeComponent extends GenericReferentialComponent<ProvisionFamilyType> implements OnInit {
  constructor(private provisionFamilyTypeService: ProvisionFamilyTypeService,
    private formBuilder2: FormBuilder,
    private appService2: AppService,) {
    super(formBuilder2, appService2);
  }

  getAddOrUpdateObservable(): Observable<ProvisionFamilyType> {
    return this.provisionFamilyTypeService.addOrUpdateProvisionFamilyType(this.selectedEntity!);
  }
  getGetObservable(): Observable<ProvisionFamilyType[]> {
    return this.provisionFamilyTypeService.getProvisionFamilyTypes();
  }
}
