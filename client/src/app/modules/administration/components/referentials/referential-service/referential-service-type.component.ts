import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { PROVISION_SCREEN_TYPE_ANNOUNCEMENT } from 'src/app/libs/Constants';
import { ConstantService } from 'src/app/modules/miscellaneous/services/constant.service';
import { AssoServiceProvisionType } from 'src/app/modules/quotation/model/AssoServiceProvisionType';
import { AssoServiceTypeDocument } from 'src/app/modules/quotation/model/AssoServiceTypeDocument';
import { ServiceType } from 'src/app/modules/quotation/model/ServiceType';
import { NoticeTypeService } from 'src/app/modules/quotation/services/notice.type.service';
import { ServiceFieldTypeService } from 'src/app/modules/quotation/services/service.field.type.service';
import { ServiceTypeService } from 'src/app/modules/quotation/services/service.type.service';
import { AppService } from 'src/app/services/app.service';
import { AssoServiceTypeFieldType } from '../../../../quotation/model/AssoServiceTypeFieldType';
import { GenericReferentialComponent } from '../generic-referential/generic-referential-component';

@Component({
  selector: 'referential-service-type',
  templateUrl: './referential-service-type.component.html',
  styleUrls: ['./../generic-referential/generic-referential.component.css']
})
export class ReferentialServiceTypeComponent extends GenericReferentialComponent<ServiceType> implements OnInit {
  constructor(private serviceService: ServiceTypeService,
    private noticeTypeService: NoticeTypeService,
    private serviceFieldTypeService: ServiceFieldTypeService,
    private formBuilder2: FormBuilder,
    private constantService: ConstantService,
    private appService2: AppService,) {
    super(formBuilder2, appService2);
  }

  PROVISION_SCREEN_TYPE_ANNOUNCEMENT = PROVISION_SCREEN_TYPE_ANNOUNCEMENT;
  deleteIndex: number = 1;

  getAddOrUpdateObservable(): Observable<ServiceType> {
    return this.serviceService.addOrUpdateServiceType(this.selectedEntity!);
  }

  getGetObservable(): Observable<ServiceType[]> {
    return this.serviceService.getServiceTypesComplete();
  }

  addProvisionType() {
    if (this.selectedEntity)
      if (!this.selectedEntity.assoServiceProvisionTypes)
        this.selectedEntity.assoServiceProvisionTypes = [] as Array<AssoServiceProvisionType>;
    this.selectedEntity?.assoServiceProvisionTypes.push({} as AssoServiceProvisionType);
  }

  deleteProvisionType(assoServiceProvisionType: AssoServiceProvisionType) {
    this.deleteIndex++;
    if (this.selectedEntity && this.selectedEntity.assoServiceProvisionTypes)
      for (let i = 0; i < this.selectedEntity.assoServiceProvisionTypes.length; i++)
        if (this.selectedEntity.assoServiceProvisionTypes[i].provisionType.id == assoServiceProvisionType.provisionType.id)
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
    this.deleteIndex++;
    if (this.selectedEntity && this.selectedEntity.assoServiceTypeDocuments)
      for (let i = 0; i < this.selectedEntity.assoServiceTypeDocuments.length; i++)
        if (this.selectedEntity.assoServiceTypeDocuments[i].typeDocument.code == assoServiceTypeDocument.typeDocument.code)
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
    this.deleteIndex++;
    if (this.selectedEntity && this.selectedEntity.assoServiceTypeFieldTypes)
      for (let i = 0; i < this.selectedEntity.assoServiceTypeFieldTypes.length; i++)
        if (this.selectedEntity.assoServiceTypeFieldTypes[i].serviceFieldType.id == assoServiceFieldType.serviceFieldType.id)
          this.selectedEntity.assoServiceTypeFieldTypes.splice(i, 1);
  }

  cloneEntity(): void {
    this.selectedEntity = structuredClone(this.selectedEntity);
    (this.selectedEntity as any).id = undefined;

    if (this.selectedEntity) {
      if (this.selectedEntity.assoServiceProvisionTypes)
        for (let i = 0; i < this.selectedEntity.assoServiceProvisionTypes.length; i++)
          (this.selectedEntity.assoServiceProvisionTypes[i] as any).id = null;

      if (this.selectedEntity.assoServiceTypeDocuments)
        for (let i = 0; i < this.selectedEntity.assoServiceTypeDocuments.length; i++)
          (this.selectedEntity.assoServiceTypeDocuments[i] as any).id = null;

      if (this.selectedEntity.assoServiceTypeFieldTypes)
        for (let i = 0; i < this.selectedEntity.assoServiceTypeFieldTypes.length; i++)
          (this.selectedEntity.assoServiceTypeFieldTypes[i] as any).id = null;
    }
    this.entities.push(this.selectedEntity!);
    this.setDataTable();
  }

  changeProvisionFamilyType(assoServiceProvisionType: AssoServiceProvisionType) {
    if (assoServiceProvisionType) {
      if (assoServiceProvisionType.noticeType && assoServiceProvisionType.noticeTypeFamily && assoServiceProvisionType.noticeType.noticeTypeFamily.id != assoServiceProvisionType.noticeTypeFamily.id)
        assoServiceProvisionType.noticeType = undefined!;
    }
  }

}
