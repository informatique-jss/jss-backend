import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { GenericSelectComponent } from '../generic-select/generic-select.component';
import { UserNoteService } from '../../../../../services/user.notes.service';
import { AssoRadioValueServiceTypeFieldType } from '../../../../quotation/model/AssoRadioValueServiceTypeFieldType';
import { AssoRadioValueServiceTypeFieldTypeService } from '../../../../quotation/services/asso.radio.value.service.type.field.type.service';

@Component({
  selector: 'select-radio-value-service-field-type',
  templateUrl: '../generic-select/generic-select.component.html',
  styleUrls: ['../generic-select/generic-select.component.css']
})

export class SelectRadioValueServiceFieldTypeComponent extends GenericSelectComponent<AssoRadioValueServiceTypeFieldType> implements OnInit {
  types: AssoRadioValueServiceTypeFieldType[] = [] as Array<AssoRadioValueServiceTypeFieldType>;

  constructor(private formBuild: UntypedFormBuilder,
    private userNoteService2: UserNoteService,
    private assoRadioValueServiceTypeFieldTypeService: AssoRadioValueServiceTypeFieldTypeService) {
    super(formBuild, userNoteService2)
  }

  initTypes(): void {
    this.types = [];
    this.assoRadioValueServiceTypeFieldTypeService.getAssoRadioValueServiceTypeFieldTypes().subscribe(response => {
      if (response)
        this.types = response;
    });
  }
}
