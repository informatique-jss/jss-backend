import { Component, Input, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { GenericSelectComponent } from '../generic-select/generic-select.component';
import { UserNoteService } from '../../../../../services/user.notes.service';
import { ServiceFieldTypePossibleValue } from 'src/app/modules/quotation/model/ServiceFieldTypePossibleValue';
import { ServiceFieldType } from 'src/app/modules/quotation/model/ServiceFieldType';

@Component({
  selector: 'select-value-service-field-type',
  templateUrl: '../generic-select/generic-select.component.html',
  styleUrls: ['../generic-select/generic-select.component.css']
})

export class SelectValueServiceFieldTypeComponent extends GenericSelectComponent<ServiceFieldTypePossibleValue> implements OnInit {
  types: ServiceFieldTypePossibleValue[] = [] as Array<ServiceFieldTypePossibleValue>;

  @Input() serviceFieldType: ServiceFieldType | undefined;

  constructor(private formBuild: UntypedFormBuilder,
    private userNoteService2: UserNoteService) {
    super(formBuild, userNoteService2)
  }

  initTypes(): void {
    this.types = [];
    if (this.serviceFieldType && this.serviceFieldType.serviceFieldTypePossibleValues)
      this.types.push(...this.serviceFieldType.serviceFieldTypePossibleValues.sort((a: ServiceFieldTypePossibleValue, b: ServiceFieldTypePossibleValue) => {
        return a.value.localeCompare(b.value);
      }));
  }
}
