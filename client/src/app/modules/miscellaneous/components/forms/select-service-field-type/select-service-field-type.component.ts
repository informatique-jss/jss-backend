import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { GenericSelectComponent } from '../generic-select/generic-select.component';
import { DATE_SERVICE_FIELD_TYPE, INTEGER_SERVICE_FIELD_TYPE, RADIO_SERVICE_FIELD_TYPE, TEXT_SERVICE_FIELD_TYPE } from '../../../../../libs/Constants';

@Component({
  selector: 'select-service-field-type',
  templateUrl: '../generic-select/generic-select.component.html',
  styleUrls: ['../generic-select/generic-select.component.css']
})

export class SelectServiceFieldTypeComponent extends GenericSelectComponent<string> implements OnInit {
  types: string[] = [] as Array<string>;

  constructor(private formBuild: UntypedFormBuilder,
    private userNoteService2: UserNoteService) {
    super(formBuild, userNoteService2)
  }

  initTypes(): void {
    this.types = [];
    this.types.push(INTEGER_SERVICE_FIELD_TYPE);
    this.types.push(TEXT_SERVICE_FIELD_TYPE);
    this.types.push(DATE_SERVICE_FIELD_TYPE);
    this.types.push(RADIO_SERVICE_FIELD_TYPE);
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
}
