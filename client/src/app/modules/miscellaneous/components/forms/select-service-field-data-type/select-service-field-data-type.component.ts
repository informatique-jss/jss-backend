import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { GenericSelectComponent } from '../generic-select/generic-select.component';
import { SERVICE_FIELD_TYPE_DATE, SERVICE_FIELD_TYPE_INTEGER, SERVICE_FIELD_TYPE_SELECT, SERVICE_FIELD_TYPE_TEXT, SERVICE_FIELD_TYPE_TEXTAREA } from '../../../../../libs/Constants';

@Component({
  selector: 'select-service-field-data-type',
  templateUrl: '../generic-select/generic-select.component.html',
  styleUrls: ['../generic-select/generic-select.component.css']
})

export class SelectServiceFieldDataTypeComponent extends GenericSelectComponent<string> implements OnInit {
  types: string[] = [] as Array<string>;

  constructor(private formBuild: UntypedFormBuilder,) {
    super(formBuild,)
  }

  initTypes(): void {
    this.types = [];
    this.types.push(SERVICE_FIELD_TYPE_INTEGER);
    this.types.push(SERVICE_FIELD_TYPE_TEXT);
    this.types.push(SERVICE_FIELD_TYPE_DATE);
    this.types.push(SERVICE_FIELD_TYPE_SELECT);
    this.types.push(SERVICE_FIELD_TYPE_TEXTAREA);
    this.types = this.types.sort((a, b) => a.localeCompare(b));
  }

  compareWithId = this.compareWithLabel;

  compareWithLabel(o1: any, o2: any): boolean {
    if (o1 == null && o2 != null || o1 != null && o2 == null)
      return false;
    if (o1 && o2)
      return o1 == o2;
    return false
  }

  displayLabel(object: string): string {
    if (!object)
      return "";

    if (object == SERVICE_FIELD_TYPE_INTEGER)
      return "Nombre";
    if (object == SERVICE_FIELD_TYPE_DATE)
      return "Date";
    if (object == SERVICE_FIELD_TYPE_SELECT)
      return "Select";
    if (object == SERVICE_FIELD_TYPE_TEXT)
      return "Texte simple";
    if (object == SERVICE_FIELD_TYPE_TEXTAREA)
      return "Texte long";
    return "";
  }
}
