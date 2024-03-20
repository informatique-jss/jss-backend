import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { ConstantService } from 'src/app/modules/miscellaneous/services/constant.service';
import { AssoServiceProvisionType } from 'src/app/modules/quotation/model/AssoServiceProvisionType';
import { AssoServiceTypeDocument } from 'src/app/modules/quotation/model/AssoServiceTypeDocument';
import { ServiceType } from 'src/app/modules/quotation/model/ServiceType';
import { ServiceTypeService } from 'src/app/modules/quotation/services/service.type.service';
import { AppService } from 'src/app/services/app.service';
import { GenericReferentialComponent } from '../generic-referential/generic-referential-component';

@Component({
  selector: 'referential-service-type',
  templateUrl: './referential-service-type.component.html',
  styleUrls: ['./../generic-referential/generic-referential.component.css']
})
export class ReferentialServiceTypeComponent extends GenericReferentialComponent<ServiceType> implements OnInit {
  constructor(private serviceService: ServiceTypeService,
    private formBuilder2: FormBuilder,
    private constantService: ConstantService,
    private appService2: AppService,) {
    super(formBuilder2, appService2);
  }

  getAddOrUpdateObservable(): Observable<ServiceType> {
    return this.serviceService.addOrUpdateServiceType(this.selectedEntity!);
  }
  getGetObservable(): Observable<ServiceType[]> {
    return this.serviceService.getServiceTypes();
  }

  addProvisionType() {
    if (this.selectedEntity)
      if (!this.selectedEntity.assoServiceProvisionTypes)
        this.selectedEntity.assoServiceProvisionTypes = [] as Array<AssoServiceProvisionType>;
    this.selectedEntity?.assoServiceProvisionTypes.push({} as AssoServiceProvisionType);
  }

  deleteProvisionType(assoServiceProvisionType: AssoServiceProvisionType) {
    if (this.selectedEntity && this.selectedEntity.assoServiceProvisionTypes)
      for (let i = 0; i < this.selectedEntity.assoServiceProvisionTypes.length; i++)
        if (this.selectedEntity.assoServiceProvisionTypes[i].id == assoServiceProvisionType.id)
          this.selectedEntity.assoServiceProvisionTypes.splice(i, 1);
  }

  addTypeDocument() {
    if (this.selectedEntity)
      if (!this.selectedEntity.assoServiceTypeDocuments)
        this.selectedEntity.assoServiceTypeDocuments = [] as Array<AssoServiceTypeDocument>;
    this.selectedEntity?.assoServiceTypeDocuments.push({} as AssoServiceTypeDocument);
  }

  deleteTypeDocument(assoServiceTypeDocument: AssoServiceTypeDocument) {
    if (this.selectedEntity && this.selectedEntity.assoServiceTypeDocuments)
      for (let i = 0; i < this.selectedEntity.assoServiceTypeDocuments.length; i++)
        if (this.selectedEntity.assoServiceTypeDocuments[i].id == assoServiceTypeDocument.id)
          this.selectedEntity.assoServiceTypeDocuments.splice(i, 1);
  }
}
