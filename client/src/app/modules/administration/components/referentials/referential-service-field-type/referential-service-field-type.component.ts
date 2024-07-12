import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { AppService } from 'src/app/services/app.service';
import { ServiceFieldType } from 'src/app/modules/quotation/model/ServiceFieldType';
import { ServiceFieldTypeService } from 'src/app/modules/quotation/services/service.field.type.service';
import { GenericReferentialComponent } from '../generic-referential/generic-referential-component';

@Component({
  selector: 'referential-service-field-type',
  templateUrl: './referential-service-field-type.component.html',
  styleUrls: ['./../generic-referential/generic-referential.component.css']
})
export class ReferentialServiceFieldTypeComponent extends GenericReferentialComponent<ServiceFieldType> implements OnInit {
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
}
