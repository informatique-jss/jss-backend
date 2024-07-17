import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { AppService } from 'src/app/services/app.service';
import { ServiceFieldType } from 'src/app/modules/quotation/model/ServiceFieldType';
import { ServiceFieldTypeService } from 'src/app/modules/quotation/services/service.field.type.service';
import { GenericReferentialComponent } from '../generic-referential/generic-referential-component';
import { SERVICE_FIELD_TYPE_SELECT } from 'src/app/libs/Constants';
import { AssoServiceProvisionType } from 'src/app/modules/quotation/model/AssoServiceProvisionType';
import { ServiceFieldTypePossibleValue } from 'src/app/modules/quotation/model/ServiceFieldTypePossibleValue';

@Component({
  selector: 'referential-service-field-type',
  templateUrl: './referential-service-field-type.component.html',
  styleUrls: ['./../generic-referential/generic-referential.component.css']
})
export class ReferentialServiceFieldTypeComponent extends GenericReferentialComponent<ServiceFieldType> implements OnInit {

  SERVICE_FIELD_TYPE_SELECT = SERVICE_FIELD_TYPE_SELECT;

  constructor(private serviceFieldTypeService: ServiceFieldTypeService,
    private formBuilder2: FormBuilder,
    private appService2: AppService,) {
    super(formBuilder2, appService2);
  }

  getAddOrUpdateObservable(): Observable<ServiceFieldType> {
    return this.serviceFieldTypeService.addOrUpdateServiceFieldType(this.selectedEntity!);
  }
  getGetObservable(): Observable<ServiceFieldType[]> {
    return this.serviceFieldTypeService.getServiceFieldTypes();
  }

  addPossibleValue() {
    if (this.selectedEntity) {
      if (!this.selectedEntity.serviceFieldTypePossibleValues)
        this.selectedEntity.serviceFieldTypePossibleValues = [] as Array<ServiceFieldTypePossibleValue>;
      this.selectedEntity.serviceFieldTypePossibleValues.push({} as ServiceFieldTypePossibleValue);
    }
  }

  deletePossibleValue(serviceFieldTypePossibleValue: ServiceFieldTypePossibleValue) {
    if (this.selectedEntity && this.selectedEntity.serviceFieldTypePossibleValues)
      for (let i = 0; i < this.selectedEntity.serviceFieldTypePossibleValues.length; i++)
        if (this.selectedEntity.serviceFieldTypePossibleValues[i].value == serviceFieldTypePossibleValue.value)
          this.selectedEntity.serviceFieldTypePossibleValues.splice(i, 1);
  }
}
