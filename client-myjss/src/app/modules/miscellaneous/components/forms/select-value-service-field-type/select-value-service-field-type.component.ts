import { Component, Input, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { ServiceFieldType } from '../../../../my-account/model/ServiceFieldType';
import { ServiceTypeFieldTypePossibleValue } from '../../../../my-account/model/ServiceTypeFieldTypePossibleValue';
import { GenericSelectComponent } from '../generic-select/generic-select.component';

@Component({
  selector: 'select-value-service-field-type',
  templateUrl: '../generic-select/generic-select.component.html',
  styleUrls: ['../generic-select/generic-select.component.css'],
  standalone: false,
})

export class SelectValueServiceFieldTypeComponent extends GenericSelectComponent<ServiceTypeFieldTypePossibleValue> implements OnInit {
  types: ServiceTypeFieldTypePossibleValue[] = [] as Array<ServiceTypeFieldTypePossibleValue>;

  @Input() serviceFieldType: ServiceFieldType | undefined;

  constructor(private formBuild: UntypedFormBuilder) {
    super(formBuild)
  }

  initTypes(): void {
    this.types = [];
    if (this.serviceFieldType && this.serviceFieldType.serviceFieldTypePossibleValues)
      this.types.push(...this.serviceFieldType.serviceFieldTypePossibleValues.sort((a: ServiceTypeFieldTypePossibleValue, b: ServiceTypeFieldTypePossibleValue) => {
        return a.value.localeCompare(b.value);
      }));
  }

  override displayLabel(object: any): string {
    if (object && object.value)
      return object.value;
    if (typeof object === "string")
      return object;
    return "";
  }
}
