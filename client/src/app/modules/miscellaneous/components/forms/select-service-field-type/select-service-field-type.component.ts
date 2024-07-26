import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { GenericSelectComponent } from '../generic-select/generic-select.component';
import { ServiceFieldType } from '../../../../quotation/model/ServiceFieldType';
import { ServiceFieldTypeService } from '../../../../quotation/services/service.field.type.service';

@Component({
  selector: 'select-service-field-type',
  templateUrl: '../generic-select/generic-select.component.html',
  styleUrls: ['../generic-select/generic-select.component.css']
})

export class SelectServiceFieldTypeComponent extends GenericSelectComponent<ServiceFieldType> implements OnInit {
  types: ServiceFieldType[] = [] as Array<ServiceFieldType>;

  constructor(private formBuild: UntypedFormBuilder,
    private serviceFieldTypeService: ServiceFieldTypeService) {
    super(formBuild)
  }

  initTypes(): void {
    this.types = [];
    this.serviceFieldTypeService.getServiceFieldTypes().subscribe(response => {
      if (response)
        this.types = response;
    });
  }
}
