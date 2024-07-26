import { Component, Input, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { GenericSelectComponent } from '../generic-select/generic-select.component';
import { ServiceTypeFieldTypePossibleValue } from 'src/app/modules/quotation/model/ServiceTypeFieldTypePossibleValue';
import { ServiceFieldType } from 'src/app/modules/quotation/model/ServiceFieldType';

@Component({
  selector: 'select-value-service-field-type',
  templateUrl: '../generic-select/generic-select.component.html',
  styleUrls: ['../generic-select/generic-select.component.css']
})

export class SelectValueServiceFieldTypeComponent extends GenericSelectComponent<ServiceTypeFieldTypePossibleValue> implements OnInit {
  types: ServiceTypeFieldTypePossibleValue[] = [] as Array<ServiceTypeFieldTypePossibleValue>;

  @Input() serviceFieldType: ServiceFieldType | undefined;

  constructor(private formBuild: UntypedFormBuilder,) {
    super(formBuild,)
  }

  initTypes(): void {
    this.types = [];
    if (this.serviceFieldType && this.serviceFieldType.serviceFieldTypePossibleValues)
      this.types.push(...this.serviceFieldType.serviceFieldTypePossibleValues.sort((a: ServiceTypeFieldTypePossibleValue, b: ServiceTypeFieldTypePossibleValue) => {
        return a.value.localeCompare(b.value);
      }));
  }

  displayLabel(object: any): string {
    if (object && object.value)
      return object.value;
    if (typeof object === "string")
      return object;
    return "";
  }
}
