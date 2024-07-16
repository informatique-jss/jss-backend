import { Component, Input, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { ConstantService } from 'src/app/modules/miscellaneous/services/constant.service';
import { AssoServiceProvisionType } from 'src/app/modules/quotation/model/AssoServiceProvisionType';
import { AssoServiceTypeDocument } from 'src/app/modules/quotation/model/AssoServiceTypeDocument';
import { ServiceType } from 'src/app/modules/quotation/model/ServiceType';
import { ServiceTypeService } from 'src/app/modules/quotation/services/service.type.service';
import { AppService } from 'src/app/services/app.service';
import { GenericReferentialComponent } from '../generic-referential/generic-referential-component';
import { AssoServiceTypeFieldType } from '../../../../quotation/model/AssoServiceTypeFieldType';
import { ServiceFieldTypeService } from 'src/app/modules/quotation/services/service.field.type.service';
import { ServiceFieldType } from 'src/app/modules/quotation/model/ServiceFieldType';
import { SERVICE_FIELD_TYPE_DATE, SERVICE_FIELD_TYPE_INTEGER, SERVICE_FIELD_TYPE_RADIO, SERVICE_FIELD_TYPE_TEXT, SERVICE_FIELD_TYPE_TEXTAREA } from 'src/app/libs/Constants';

@Component({
  selector: 'referential-service-type',
  templateUrl: './referential-service-type.component.html',
  styleUrls: ['./../generic-referential/generic-referential.component.css']
})
export class ReferentialServiceTypeComponent extends GenericReferentialComponent<ServiceType> implements OnInit {
  constructor(private serviceService: ServiceTypeService,
    private serviceFieldTypeService: ServiceFieldTypeService,
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
    if (this.selectedEntity) {
      if (!this.selectedEntity.assoServiceTypeDocuments)
        this.selectedEntity.assoServiceTypeDocuments = [] as Array<AssoServiceTypeDocument>;
      this.selectedEntity.assoServiceTypeDocuments.push({} as AssoServiceTypeDocument);
    }
  }

  deleteTypeDocument(assoServiceTypeDocument: AssoServiceTypeDocument) {
    if (this.selectedEntity && this.selectedEntity.assoServiceTypeDocuments)
      for (let i = 0; i < this.selectedEntity.assoServiceTypeDocuments.length; i++)
        if (this.selectedEntity.assoServiceTypeDocuments[i].id == assoServiceTypeDocument.id)
          this.selectedEntity.assoServiceTypeDocuments.splice(i, 1);
  }

  addFieldType() {
    if (this.selectedEntity) {
      if (!this.selectedEntity.assoServiceTypeFieldTypes)
        this.selectedEntity.assoServiceTypeFieldTypes = [] as Array<AssoServiceTypeFieldType>;
      this.selectedEntity.assoServiceTypeFieldTypes.push({} as AssoServiceTypeFieldType);
    }
  }

  deleteFieldType(assoServiceFieldType: AssoServiceTypeFieldType) {
    if (this.selectedEntity && this.selectedEntity.assoServiceTypeFieldTypes)
      for (let i = 0; i < this.selectedEntity.assoServiceTypeFieldTypes.length; i++)
        if (this.selectedEntity.assoServiceTypeFieldTypes[i].id == assoServiceFieldType.id)
          this.selectedEntity.assoServiceTypeFieldTypes.splice(i, 1);
  }
  displayLabel(object: string): string {
    if (object == SERVICE_FIELD_TYPE_INTEGER)
      return "Nombre";
    if (object == SERVICE_FIELD_TYPE_DATE)
      return "Date";
    if (object == SERVICE_FIELD_TYPE_RADIO)
      return "Radio";
    if (object == SERVICE_FIELD_TYPE_TEXT)
      return "Texte simple";
    if (object == SERVICE_FIELD_TYPE_TEXTAREA)
      return "Texte long";
    return "";
  }
}
